package ru.yandex.practicum.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.order.ProductReturnRequest;
import ru.yandex.practicum.feign.OrderClient;
import ru.yandex.practicum.service.OrderService;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/order")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderController implements OrderClient {
    final OrderService orderService;

    @Override
    public List<OrderDto> getClientOrders(String username) {
        log.info("Запрос на получение заказов пользователя {}", username);
        return orderService.getClientOrders(username);
    }

    @Override
    public OrderDto createNewOrder(CreateNewOrderRequest request) {
        log.info("Запрос на создание нового заказа {}", request);
        return orderService.createNewOrder(request);
    }

    @Override
    public OrderDto productReturn(ProductReturnRequest request) {
        log.info("Запрос на возврат заказа с id = {}", request.getOrderId());
        return orderService.productReturn(request);
    }

    @Override
    public OrderDto payment(UUID orderId) {
        log.info("Запрос на оплату заказа с id = {}", orderId);
        return orderService.payment(orderId);
    }

    @Override
    public OrderDto paymentFailed(UUID orderId) {
        log.info("Запрос на отмену оплаты заказа с id = {}", orderId);
        return orderService.paymentFailed(orderId);
    }

    @Override
    public OrderDto delivery(UUID orderId) {
        log.info("Запрос на доставку заказа с id = {}", orderId);
        return orderService.delivery(orderId);
    }

    @Override
    public OrderDto deliveryFailed(UUID orderId) {
        log.info("Запрос на отмену доставки заказа с id = {}", orderId);
        return orderService.deliveryFailed(orderId);
    }

    @Override
    public OrderDto completeOrder(UUID orderId) {
        log.info("Запрос на завершение заказа с id = {}", orderId);
        return orderService.completeOrder(orderId);
    }

    @Override
    public OrderDto calculateTotalCost(UUID orderId) {
        log.info("Запрос на расчет стоимости заказа с id = {}", orderId);
        return orderService.calculateTotalCost(orderId);
    }

    @Override
    public OrderDto calculateDeliveryCost(UUID orderId) {
        log.info("Запрос на расчет стоимости доставки заказа с id = {}", orderId);
        return orderService.calculateDeliveryCost(orderId);
    }

    @Override
    public OrderDto assembly(UUID orderId) {
        log.info("Запрос на сборку заказа с id = {}", orderId);
        return orderService.assembly(orderId);
    }

    @Override
    public OrderDto assemblyFailed(UUID orderId) {
        log.info("Запрос на отмену сборки заказа с id = {}", orderId);
        return orderService.assemblyFailed(orderId);
    }
}