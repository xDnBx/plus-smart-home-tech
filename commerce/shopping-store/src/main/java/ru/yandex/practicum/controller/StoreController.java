package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.store.ProductDto;
import ru.yandex.practicum.dto.store.SetProductQuantityStateRequest;
import ru.yandex.practicum.dto.store.enums.ProductCategory;
import ru.yandex.practicum.service.StoreService;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/shopping-store")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StoreController {
    final StoreService storeService;

    @GetMapping
    public List<ProductDto> getProducts(@RequestParam ProductCategory category, @Valid Pageable pageable) {
        log.info("Получен запрос на список товаров категории = {}", category);
        return storeService.getProductsByCategory(category, pageable);
    }

    @PutMapping
    public ProductDto createProduct(@Valid @RequestBody ProductDto productDto) {
        log.info("Получен запрос на создание товара с именем = {}", productDto.getProductName());
        return storeService.createProduct(productDto);
    }

    @PostMapping
    public ProductDto updateProduct(@RequestBody ProductDto productDto) {
        log.info("Получен запрос на обновление товара с именем = {}", productDto.getProductName());
        return storeService.updateProduct(productDto);
    }

    @PostMapping("/removeProductFromStore")
    public Boolean removeProduct(@RequestBody UUID productId) {
        log.info("Получен запрос на удаление товара с id = {}", productId);
        return storeService.removeProduct(productId);
    }

    @PostMapping("/quantityState")
    public Boolean setQuantityState(@Valid SetProductQuantityStateRequest request) {
        log.info("Получен запрос на установку количества товара с id = {}", request.getProductId());
        return storeService.setQuantityState(request);
    }

    @GetMapping("/{productId}")
    public ProductDto getProductById(@PathVariable UUID productId) {
        log.info("Получен запрос на получение товара с id = {}", productId);
        return storeService.getProductById(productId);
    }
}