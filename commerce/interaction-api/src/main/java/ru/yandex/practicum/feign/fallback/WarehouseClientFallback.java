package ru.yandex.practicum.feign.fallback;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.*;
import ru.yandex.practicum.exception.WarehouseUnavailableException;
import ru.yandex.practicum.feign.WarehouseClient;

import java.util.Map;
import java.util.UUID;

@Component
public class WarehouseClientFallback implements WarehouseClient {
    @Override
    public void newProductInWarehouse(NewProductInWarehouseRequest request) {
        throw new WarehouseUnavailableException("Сервер склада недоступен");
    }

    @Override
    public void shipToDelivery(ShippedToDeliveryRequest request) {
        throw new WarehouseUnavailableException("Сервер склада недоступен");
    }

    @Override
    public void acceptReturn(Map<UUID, Long> returnedProducts) {
        throw new WarehouseUnavailableException("Сервер склада недоступен");
    }

    @Override
    public BookedProductsDto checkProductQuantity(ShoppingCartDto shoppingCart) {
        throw new WarehouseUnavailableException("Сервер склада недоступен");
    }

    @Override
    public BookedProductsDto assembleProducts(AssemblyProductsForOrderRequest request) {
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