package ru.yandex.practicum.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.store.ProductDto;
import ru.yandex.practicum.dto.store.SetProductQuantityStateRequest;
import ru.yandex.practicum.dto.store.enums.ProductCategory;
import ru.yandex.practicum.dto.store.enums.ProductState;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.mapper.StoreMapper;
import ru.yandex.practicum.model.Product;
import ru.yandex.practicum.repository.StoreRepository;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StoreServiceImpl implements StoreService {
    final StoreRepository storeRepository;
    final StoreMapper storeMapper;

    @Override
    public List<ProductDto> getProductsByCategory(ProductCategory category, Pageable pageable) {
        log.info("Получение списка товаров по категории = {}", category);
        return storeRepository.findAllByProductCategory(category, pageable).stream()
                .map(storeMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public ProductDto createProduct(ProductDto productDto) {
        log.info("Создание товара с именем = {}", productDto.getProductName());
        Product product = storeMapper.toEntity(productDto);
        product.setProductState(ProductState.ACTIVE);
        return storeMapper.toDto(storeRepository.save(product));
    }

    @Override
    @Transactional
    public ProductDto updateProduct(ProductDto productDto) {
        findProductById(productDto.getProductId());
        log.info("Обновление товара с именем = {}", productDto.getProductName());
        return storeMapper.toDto(storeRepository.save(storeMapper.toEntity(productDto)));
    }

    @Override
    @Transactional
    public Boolean removeProduct(UUID productId) {
        Product product = findProductById(productId);
        if (product.getProductState().equals(ProductState.DEACTIVATE)) {
            log.error("Ошибка удаления товара с именем = {}, т.к. товар уже удален", product.getProductName());
            return false;
        }
        product.setProductState(ProductState.DEACTIVATE);
        storeRepository.save(product);
        log.info("Товар с именем = {} успешно удален", product.getProductName());
        return true;
    }

    @Override
    @Transactional
    public Boolean setQuantityState(SetProductQuantityStateRequest request) {
        Product product = findProductById(request.getProductId());
        if (product.getQuantityState().equals(request.getQuantityState())) {
            log.error("Ошибка изменения количества товара с именем = {}, оно не изменилось", product.getProductName());
            return false;
        }
        log.info("Изменение количества товара с именем = {} на указанное значение = {}", product.getProductName(),
                request.getQuantityState());
        product.setQuantityState(request.getQuantityState());
        storeRepository.save(product);
        log.info("Количество товара с именем = {} успешно изменено", product.getProductName());
        return true;
    }

    @Override
    public ProductDto getProductById(UUID productId) {
        log.info("Получение товара с id = {}", productId);
        return storeMapper.toDto(findProductById(productId));
    }

    private Product findProductById(UUID productId) {
        log.info("Поиск товара с id = {}", productId);
        return storeRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Товар с id = " + productId + " не найден"));
    }
}