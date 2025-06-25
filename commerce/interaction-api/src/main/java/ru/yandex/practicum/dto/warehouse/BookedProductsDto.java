package ru.yandex.practicum.dto.warehouse;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookedProductsDto {
    @NotBlank
    Double deliveryWeight;

    @NotBlank
    Double deliveryVolume;

    @NotBlank
    Boolean fragile;
}