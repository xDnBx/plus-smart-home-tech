package ru.yandex.practicum.dto.order;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductReturnRequest {
    @NotBlank
    UUID orderId;

    @NotBlank
    Map<UUID, Long> products;
}