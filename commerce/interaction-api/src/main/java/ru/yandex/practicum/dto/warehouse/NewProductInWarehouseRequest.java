package ru.yandex.practicum.dto.warehouse;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewProductInWarehouseRequest {
    @NotBlank
    UUID productId;

    Boolean fragile;

    @NotBlank
    DimensionDto dimension;

    @NotBlank
    @Min(value = 1, message = "Вес не может быть меньше 1")
    Double weight;
}