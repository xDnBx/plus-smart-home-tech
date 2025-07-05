package ru.yandex.practicum.dto.store;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.dto.store.enums.ProductCategory;
import ru.yandex.practicum.dto.store.enums.ProductState;
import ru.yandex.practicum.dto.store.enums.QuantityState;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductDto {
    UUID productId;

    @NotBlank
    String productName;

    @NotBlank
    String description;

    String imageSrc;

    @NotNull
    QuantityState quantityState;

    @NotNull
    ProductState productState;

    ProductCategory productCategory;

    @NotNull
    @Min(value = 1, message = "Цена должна быть больше или равна 1")
    Float price;
}