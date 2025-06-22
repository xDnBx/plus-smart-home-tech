package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.model.ShoppingCart;

@Mapper
public interface CartMapper {
    ShoppingCartDto toDto(ShoppingCart shoppingCart);
}