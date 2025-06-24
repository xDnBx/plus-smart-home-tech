package ru.yandex.practicum.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.dto.store.ListProductsResponse;
import ru.yandex.practicum.dto.store.ProductDto;
import ru.yandex.practicum.dto.store.SetProductQuantityStateRequest;
import ru.yandex.practicum.dto.store.enums.ProductCategory;
import ru.yandex.practicum.feign.StoreClient;
import ru.yandex.practicum.service.StoreService;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/shopping-store")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StoreController implements StoreClient {
    final StoreService storeService;

    @Override
    public ListProductsResponse getProducts(ProductCategory category, Pageable pageable) {
        log.info("Получен запрос на список товаров категории = {}", category);
        return storeService.getProductsByCategory(category, pageable);
    }

    @Override
    public ProductDto createProduct(ProductDto productDto) {
        log.info("Получен запрос на создание товара с именем = {}", productDto.getProductName());
        return storeService.createProduct(productDto);
    }

    @Override
    public ProductDto updateProduct(ProductDto productDto) {
        log.info("Получен запрос на обновление товара с именем = {}", productDto.getProductName());
        return storeService.updateProduct(productDto);
    }

    @Override
    public Boolean removeProduct(UUID productId) {
        log.info("Получен запрос на удаление товара с id = {}", productId);
        return storeService.removeProduct(productId);
    }

    @Override
    public Boolean setQuantityState(SetProductQuantityStateRequest request) {
        log.info("Получен запрос на установку количества товара с id = {}", request.getProductId());
        return storeService.setQuantityState(request);
    }

    @Override
    public ProductDto getProductById(UUID productId) {
        log.info("Получен запрос на получение товара с id = {}", productId);
        return storeService.getProductById(productId);
    }
}