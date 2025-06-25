package ru.yandex.practicum.feign.fallback;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.dto.warehouse.AddressDto;
import ru.yandex.practicum.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.dto.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.exception.WarehouseUnavailableException;
import ru.yandex.practicum.feign.WarehouseClient;

@Component
public class WarehouseClientFallback implements WarehouseClient {
    @Override
    public void newProductInWarehouse(NewProductInWarehouseRequest request) {
        throw new WarehouseUnavailableException("Сервер склада недоступен");
    }

    @Override
    public BookedProductsDto checkProductQuantity(ShoppingCartDto shoppingCart) {
        throw new WarehouseUnavailableException("Сервер склада недоступен");
    }

    @Override
    public void addProductToWarehouse(AddProductToWarehouseRequest request) {
        throw new WarehouseUnavailableException("Сервер склада недоступен");
    }

    @Override
    public AddressDto getWarehouseAddress() {
        throw new WarehouseUnavailableException("Сервер склада недоступен");
    }
}