package ru.yandex.practicum.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.delivery.enums.DeliveryState;
import ru.yandex.practicum.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.order.ProductReturnRequest;
import ru.yandex.practicum.dto.order.enums.OrderState;
import ru.yandex.practicum.dto.payment.PaymentDto;
import ru.yandex.practicum.dto.warehouse.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.exception.NoOrderFoundException;
import ru.yandex.practicum.exception.NotAuthorizedUserException;
import ru.yandex.practicum.feign.CartClient;
import ru.yandex.practicum.feign.DeliveryClient;
import ru.yandex.practicum.feign.PaymentClient;
import ru.yandex.practicum.feign.WarehouseClient;
import ru.yandex.practicum.mapper.OrderMapper;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.repository.OrderRepository;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderServiceImpl implements OrderService {
    final OrderRepository orderRepository;
    final OrderMapper orderMapper;
    final CartClient cartClient;
    final DeliveryClient deliveryClient;
    final PaymentClient paymentClient;
    final WarehouseClient warehouseClient;


    @Override
    public List<OrderDto> getClientOrders(String username) {
        checkUser(username);
        ShoppingCartDto shoppingCartDto = cartClient.getCart(username);
        return orderRepository.findByShoppingCartId(shoppingCartDto.getShoppingCartId()).stream()
                .map(orderMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public OrderDto createNewOrder(CreateNewOrderRequest request) {
        log.info("Создание нового заказа для корзины с id = {}", request.getShoppingCart().getShoppingCartId());
        Order order = Order.builder()
                .shoppingCartId(request.getShoppingCart().getShoppingCartId())
                .products(request.getShoppingCart().getProducts())
                .state(OrderState.NEW)
                .build();
        orderRepository.save(order);

        AssemblyProductsForOrderRequest assemblyRequest = AssemblyProductsForOrderRequest.builder()
                .orderId(order.getOrderId())
                .products(request.getShoppingCart().getProducts())
                .build();
        BookedProductsDto bookedProductsDto = warehouseClient.assembleProducts(assemblyRequest);

        DeliveryDto deliveryDto = DeliveryDto.builder()
                .orderId(order.getOrderId())
                .fromAddress(warehouseClient.getWarehouseAddress())
                .toAddress(request.getDeliveryAddress())
                .deliveryState(DeliveryState.CREATED)
                .build();
        deliveryDto = deliveryClient.planDelivery(deliveryDto);

        PaymentDto paymentDto = paymentClient.createPayment(orderMapper.toDto(order));

        order.setPaymentId(paymentDto.getPaymentId());
        order.setDeliveryId(deliveryDto.getDeliveryId());
        order.setDeliveryWeight(bookedProductsDto.getDeliveryWeight());
        order.setDeliveryVolume(bookedProductsDto.getDeliveryVolume());
        order.setFragile(bookedProductsDto.getFragile());
        order.setTotalPrice(paymentClient.calculateTotalCost(orderMapper.toDto(order)));
        order.setDeliveryPrice(deliveryClient.calculateDeliveryCost(orderMapper.toDto(order)));
        order.setProductPrice(paymentClient.calculateProductsCost(orderMapper.toDto(order)));

        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    public OrderDto productReturn(ProductReturnRequest request) {
        log.info("Возврат заказа с id = {} на склад", request.getOrderId());
        Order order = findOrder(request.getOrderId());
        warehouseClient.acceptReturn(request.getProducts());
        order.setState(OrderState.PRODUCT_RETURNED);
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderDto payment(UUID orderId) {
        Order order = findOrder(orderId);
        order.setState(OrderState.PAID);
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderDto paymentFailed(UUID orderId) {
        Order order = findOrder(orderId);
        order.setState(OrderState.PAYMENT_FAILED);
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    public OrderDto delivery(UUID orderId) {
        Order order = findOrder(orderId);
        deliveryClient.markAsSuccessful(orderId);
        order.setState(OrderState.DELIVERED);
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderDto deliveryFailed(UUID orderId) {
        Order order = findOrder(orderId);
        order.setState(OrderState.DELIVERY_FAILED);
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderDto completeOrder(UUID orderId) {
        Order order = findOrder(orderId);
        order.setState(OrderState.COMPLETED);
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderDto calculateTotalCost(UUID orderId) {
        log.info("Расчет стоимости заказа с id = {}", orderId);
        Order order = findOrder(orderId);
        order.setTotalPrice(paymentClient.calculateTotalCost(orderMapper.toDto(order)));
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderDto calculateDeliveryCost(UUID orderId) {
        log.info("Расчет стоимости доставки заказа с id = {}", orderId);
        Order order = findOrder(orderId);
        order.setDeliveryPrice(deliveryClient.calculateDeliveryCost(orderMapper.toDto(order)));
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderDto assembly(UUID orderId) {
        Order order = findOrder(orderId);
        order.setState(OrderState.ASSEMBLED);
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderDto assemblyFailed(UUID orderId) {
        Order order = findOrder(orderId);
        order.setState(OrderState.ASSEMBLY_FAILED);
        return orderMapper.toDto(orderRepository.save(order));
    }

    private void checkUser(String username) {
        if (username == null || username.isBlank()) {
            throw new NotAuthorizedUserException("Имя пользователя не должно быть пустым или null");
        }
    }

    private Order findOrder(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Не найден заказ с id = " + orderId));
    }
}