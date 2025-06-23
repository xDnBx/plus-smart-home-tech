package ru.yandex.practicum.dto.warehouse;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DimensionDto {
    @NotBlank
    @Min(value = 1, message = "Ширина не может быть меньше 1")
    Double width;

    @NotBlank
    @Min(value = 1, message = "Высота не может быть меньше 1")
    Double height;

    @NotBlank
    @Min(value = 1, message = "Глубина не может быть меньше 1")
    Double depth;
}