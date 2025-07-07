package ru.yandex.practicum.dto.order;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.AddressDto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateNewOrderRequest {
    @NotBlank
    ShoppingCartDto shoppingCart;

    @NotBlank
    AddressDto deliveryAddress;
}