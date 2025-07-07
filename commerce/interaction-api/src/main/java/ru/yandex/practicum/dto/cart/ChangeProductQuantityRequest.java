package ru.yandex.practicum.dto.cart;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChangeProductQuantityRequest {
    UUID productId;

    @PositiveOrZero(message = "Количество товара должно быть больше нуля")
    Long newQuantity;
}