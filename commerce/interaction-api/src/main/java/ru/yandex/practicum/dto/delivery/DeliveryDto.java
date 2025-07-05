package ru.yandex.practicum.dto.delivery;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.dto.delivery.enums.DeliveryState;
import ru.yandex.practicum.dto.warehouse.AddressDto;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeliveryDto {
    UUID deliveryId;

    @NotBlank
    AddressDto fromAddress;

    @NotBlank
    AddressDto toAddress;

    @NotBlank
    UUID orderId;

    @NotBlank
    DeliveryState deliveryState;
}