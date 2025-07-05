package ru.yandex.practicum.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.feign.DeliveryClient;
import ru.yandex.practicum.service.DeliveryService;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/delivery")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeliveryController implements DeliveryClient {
    final DeliveryService deliveryService;

    @Override
    public DeliveryDto planDelivery(DeliveryDto deliveryDto) {
        log.info("Запрос на планирование доставки заказа с id = {}", deliveryDto.getOrderId());
        return deliveryService.planDelivery(deliveryDto);
    }

    @Override
    public void markAsSuccessful(UUID orderId) {
        log.info("Запрос на успешное завершение доставки заказа с id = {}", orderId);
        deliveryService.markAsSuccessful(orderId);
    }

    @Override
    public void markAsPicked(UUID orderId) {
        log.info("Запрос на передачу заказа с id = {} в доставку", orderId);
        deliveryService.markAsPicked(orderId);
    }

    @Override
    public void markAsFailed(UUID orderId) {
        log.info("Запрос на провал доставки заказа с id = {}", orderId);
        deliveryService.markAsFailed(orderId);
    }

    @Override
    public Double calculateDeliveryCost(OrderDto orderDto) {
        log.info("Запрос на расчет стоимости доставки заказа с id = {}", orderDto.getOrderId());
        return deliveryService.calculateDeliveryCost(orderDto);
    }
}