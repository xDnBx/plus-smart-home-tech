package ru.yandex.practicum.dto.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderDto {
    @NotNull
    UUID orderId;

    @NotBlank
    UUID shoppingCartId;

    @NotEmpty
    Map<UUID, Long> products;
    UUID paymentId;
    UUID deliveryId;
    String state;
    Double deliveryWeight;
    Double deliveryVolume;
    boolean fragile;
    Double totalPrice;
    Double deliveryPrice;
    Double productPrice;
}