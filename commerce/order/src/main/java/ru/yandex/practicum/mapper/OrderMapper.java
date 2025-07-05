package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.model.Order;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    Order toEntity(CreateNewOrderRequest request);

    OrderDto toDto(Order entity);
}