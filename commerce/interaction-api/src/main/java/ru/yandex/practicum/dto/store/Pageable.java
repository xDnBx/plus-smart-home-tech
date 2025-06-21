package ru.yandex.practicum.dto.store;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Pageable {
    @Min(value = 0, message = "Параметр page не может быть отрицательным")
    @NotNull
    Integer page;

    @Min(value = 1, message = "Параметр size не может быть меньше 1")
    @NotNull
    Integer size;

    List<String> sort;
}