package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.order.OrderDto;

import java.util.UUID;

public interface DeliveryService {
    DeliveryDto planDelivery(DeliveryDto deliveryDto);

    void markAsSuccessful(UUID orderId);

    void markAsPicked(UUID orderId);

    void markAsFailed(UUID orderId);

    Double calculateDeliveryCost(OrderDto orderDto);
}