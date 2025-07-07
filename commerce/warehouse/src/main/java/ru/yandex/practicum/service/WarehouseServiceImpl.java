package ru.yandex.practicum.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.address.AddressManager;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.*;
import ru.yandex.practicum.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.mapper.WarehouseMapper;
import ru.yandex.practicum.model.Dimension;
import ru.yandex.practicum.model.OrderBooking;
import ru.yandex.practicum.model.WarehouseProduct;
import ru.yandex.practicum.repository.OrderBookingRepository;
import ru.yandex.practicum.repository.WarehouseRepository;

import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WarehouseServiceImpl implements WarehouseService {
    final WarehouseRepository warehouseRepository;
    final OrderBookingRepository orderBookingRepository;
    final WarehouseMapper warehouseMapper;

    @Override
    @Transactional
    public void newProductInWarehouse(NewProductInWarehouseRequest request) {
        if (warehouseRepository.existsById(request.getProductId())) {
            throw new SpecifiedProductAlreadyInWarehouseException(
                    "Товар уже добавлен на склад с id = " + request.getProductId());
        }
        warehouseRepository.save(warehouseMapper.toEntity(request));
        log.info("Новый товар = {} добавлен на склад", request);
    }

    @Override
    @Transactional
    public void shipToDelivery(ShippedToDeliveryRequest request) {
        log.info("Отправляем заказ с id = {} на доставку", request.getOrderId());
        OrderBooking orderBooking = orderBookingRepository.findById(request.getOrderId())
                .orElseThrow(() -> new NotFoundException("Не найдено бронирование с id = " + request.getOrderId()));
        orderBooking.setDeliveryId(request.getDeliveryId());
        orderBookingRepository.save(orderBooking);
        log.info("Заказ с id = {} отправлен на доставку", request.getOrderId());
    }

    @Override
    @Transactional
    public void acceptReturn(Map<UUID, Long> returnedProducts){
        log.info("Принимаем возврат товаров = {}", returnedProducts);
        Map<UUID, WarehouseProduct> products = warehouseRepository.findAllById(returnedProducts.keySet()).stream()
                .collect(Collectors.toMap(WarehouseProduct::getProductId, Function.identity()));
        for (Map.Entry<UUID, Long> entry : returnedProducts.entrySet()) {
            if (!products.containsKey(entry.getKey())) {
                throw new NotFoundException("Товар с id = " + entry.getKey() + " не найден на складе");
            }
            WarehouseProduct warehouseProduct = products.get(entry.getKey());
            warehouseProduct.setQuantity(warehouseProduct.getQuantity() + entry.getValue());
        }
        warehouseRepository.saveAll(products.values());
        log.info("Возврат товаров = {} принят на склад", returnedProducts);
    }

    @Override
    @Transactional(readOnly = true)
    public BookedProductsDto checkProductQuantity(ShoppingCartDto shoppingCart) {
        log.info("Проверяем наличие товаров в корзине = {} на складе", shoppingCart);
        boolean hasFragile = false;
        double totalVolume = 0;
        double totalWeight = 0;

        for (Map.Entry<UUID, Long> entry : shoppingCart.getProducts().entrySet()) {
            UUID productId = entry.getKey();
            Long requestedQuantity = entry.getValue();
            WarehouseProduct product = findProductById(productId);
            if (product.getQuantity() < requestedQuantity) {
                throw new ProductInShoppingCartLowQuantityInWarehouse(
                        "Не хватает на складе товара с id = " + productId);
            }
            if (product.isFragile()) {
                hasFragile = true;
            }
            double productVolume = calculateVolume(product.getDimension());
            totalVolume += productVolume * requestedQuantity;
            totalWeight += product.getWeight() * requestedQuantity;
        }

        return BookedProductsDto.builder()
                .fragile(hasFragile)
                .deliveryVolume(totalVolume)
                .deliveryWeight(totalWeight)
                .build();
    }

    @Override
    @Transactional
    public BookedProductsDto assembleProducts(AssemblyProductsForOrderRequest request) {
        ShoppingCartDto shoppingCart = ShoppingCartDto.builder()
                .shoppingCartId(request.getOrderId())
                .products(request.getProducts())
                .build();
        BookedProductsDto bookedProductsDto = checkProductQuantity(shoppingCart);
        OrderBooking orderBooking = OrderBooking.builder()
                .orderId(request.getOrderId())
                .products(request.getProducts())
                .build();
        orderBookingRepository.save(orderBooking);
        return bookedProductsDto;
    }

    @Override
    @Transactional
    public void addProductToWarehouse(AddProductToWarehouseRequest request) {
        WarehouseProduct product = findProductById(request.getProductId());
        product.setQuantity(product.getQuantity() + request.getQuantity());
        warehouseRepository.save(product);
        log.info("Товар с id = {} добавлен на склад", request.getProductId());
    }

    @Override
    public AddressDto getWarehouseAddress() {
        String address = AddressManager.CURRENT_ADDRESS;
        log.info("Получен адрес склада: {}", address);
        return AddressDto.builder()
                .country(address)
                .city(address)
                .street(address)
                .house(address)
                .flat(address)
                .build();
    }

    private double calculateVolume(Dimension dimension) {
        return dimension.getWidth() * dimension.getHeight() * dimension.getDepth();
    }

    private WarehouseProduct findProductById(UUID productId) {
        return warehouseRepository.findById(productId)
                .orElseThrow(() -> new NoSpecifiedProductInWarehouseException(
                        "Не найден на складе товар с id = " + productId));
    }
}
