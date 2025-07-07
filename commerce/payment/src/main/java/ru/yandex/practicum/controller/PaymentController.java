package ru.yandex.practicum.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.payment.PaymentDto;
import ru.yandex.practicum.feign.PaymentClient;
import ru.yandex.practicum.service.PaymentService;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payment")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentController implements PaymentClient {
    final PaymentService paymentService;

    @Override
    public PaymentDto createPayment(OrderDto orderDto) {
        log.info("Запрос на создание платежа для заказа с id = {}", orderDto.getOrderId());
        return paymentService.createPayment(orderDto);
    }

    @Override
    public Double calculateTotalCost(OrderDto orderDto) {
        log.info("Запрос на расчет полной стоимости заказа с id = {}", orderDto.getOrderId());
        return paymentService.calculateTotalCost(orderDto);
    }

    @Override
    public void processSuccessfulPayment(UUID paymentId) {
        log.info("Запрос на обработку успешного платежа с id = {}", paymentId);
        paymentService.processSuccessfulPayment(paymentId);
    }

    @Override
    public Double calculateProductsCost(OrderDto orderDto) {
        log.info("Запрос на расчет стоимости товаров в заказе с id = {}", orderDto.getOrderId());
        return paymentService.calculateProductsCost(orderDto);
    }

    @Override
    public void processFailedPayment(UUID paymentId) {
        log.info("Запрос на обработку неуспешного платежа с id = {}", paymentId);
        paymentService.processFailedPayment(paymentId);
    }
}