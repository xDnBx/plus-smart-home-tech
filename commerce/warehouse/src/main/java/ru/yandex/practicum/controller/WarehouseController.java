package ru.yandex.practicum.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.*;
import ru.yandex.practicum.feign.WarehouseClient;
import ru.yandex.practicum.service.WarehouseService;

import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/warehouse")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WarehouseController implements WarehouseClient {
    final WarehouseService warehouseService;

    @Override
    public void newProductInWarehouse(NewProductInWarehouseRequest request) {
        log.info("Запрос на добавление товара на склад = {}", request);
        warehouseService.newProductInWarehouse(request);
    }

    @Override
    public void shipToDelivery(ShippedToDeliveryRequest request) {
        log.info("Запрос на отправку товара в доставку = {}", request);
        warehouseService.shipToDelivery(request);
    }

    @Override
    public void acceptReturn(Map<UUID, Long> returnedProducts) {
        log.info("Запрос на возврат товаров = {} на склад", returnedProducts);
        warehouseService.acceptReturn(returnedProducts);
    }

    @Override
    public BookedProductsDto checkProductQuantity(ShoppingCartDto shoppingCart) {
        log.info("Запрос на проверку количества товаров из корзины: {}", shoppingCart);
        return warehouseService.checkProductQuantity(shoppingCart);
    }

    @Override
    public BookedProductsDto assembleProducts(AssemblyProductsForOrderRequest request) {
        log.info("Запрос на сборку для заказа = {} с товарами = {}", request.getOrderId(), request.getProducts());
        return warehouseService.assembleProducts(request);
    }

    @Override
    public void addProductToWarehouse(AddProductToWarehouseRequest request) {
        log.info("Запрос на добавление товара = {} на склад в количестве = {}", request.getProductId(),
                request.getQuantity());
        warehouseService.addProductToWarehouse(request);
    }

    @Override
    public AddressDto getWarehouseAddress() {
        log.info("Запрос на получение адреса склада");
        return warehouseService.getWarehouseAddress();
    }
}