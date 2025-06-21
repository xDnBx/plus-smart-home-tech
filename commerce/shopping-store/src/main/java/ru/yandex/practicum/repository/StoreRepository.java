package ru.yandex.practicum.repository;

import ru.yandex.practicum.dto.store.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.dto.store.enums.ProductCategory;
import ru.yandex.practicum.model.Product;

import java.util.List;
import java.util.UUID;

@Repository
public interface StoreRepository extends JpaRepository<Product, UUID> {
    List<Product> findAllByProductCategory(ProductCategory productCategory, Pageable pageable);
}