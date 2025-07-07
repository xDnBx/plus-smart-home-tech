package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.*;

import java.util.Map;
import java.util.UUID;

public interface WarehouseService {
    void newProductInWarehouse(NewProductInWarehouseRequest request);

    void shipToDelivery(ShippedToDeliveryRequest request);

    void acceptReturn(Map<UUID, Long> returnedProducts);

    BookedProductsDto checkProductQuantity(ShoppingCartDto shoppingCart);

    BookedProductsDto assembleProducts(AssemblyProductsForOrderRequest request);

    void addProductToWarehouse(AddProductToWarehouseRequest request);

    AddressDto getWarehouseAddress();
}