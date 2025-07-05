package ru.yandex.practicum.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.payment.PaymentDto;
import ru.yandex.practicum.dto.payment.enums.PaymentStatus;
import ru.yandex.practicum.dto.store.ProductDto;
import ru.yandex.practicum.exception.NotEnoughInfoInOrderToCalculateException;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.feign.OrderClient;
import ru.yandex.practicum.feign.StoreClient;
import ru.yandex.practicum.mapper.PaymentMapper;
import ru.yandex.practicum.model.Payment;
import ru.yandex.practicum.repository.PaymentRepository;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentServiceImpl implements PaymentService {
    final PaymentRepository paymentRepository;
    final PaymentMapper paymentMapper;
    final OrderClient orderClient;
    final StoreClient storeClient;

    @Value("${payment.fee_rate}")
    Double feeRate;

    @Override
    @Transactional
    public PaymentDto createPayment(OrderDto orderDto) {
        log.info("Создаем платеж для заказа с id = {}", orderDto.getOrderId());
        checkCosts(orderDto);
        Payment payment = Payment.builder()
                .orderId(orderDto.getOrderId())
                .totalPayment(orderDto.getTotalPrice())
                .deliveryTotal(orderDto.getDeliveryPrice())
                .feeTotal(orderDto.getTotalPrice() * feeRate)
                .paymentStatus(PaymentStatus.PENDING)
                .build();
        return paymentMapper.toDto(paymentRepository.save(payment));
    }

    @Override
    public Double calculateTotalCost(OrderDto orderDto) {
        log.info("Расчитываем общую стоимость заказа с id = {}", orderDto.getOrderId());
        checkCosts(orderDto);
        return orderDto.getProductPrice() + orderDto.getProductPrice() * feeRate + orderDto.getDeliveryPrice();
    }

    @Override
    @Transactional
    public void processSuccessfulPayment(UUID paymentId) {
        log.info("Обрабатываем успешный платеж с id = {}", paymentId);
        Payment payment = findPayment(paymentId);
        orderClient.payment(payment.getOrderId());
        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        paymentRepository.save(payment);
    }

    @Override
    public Double calculateProductsCost(OrderDto orderDto) {
        log.info("Расчитываем стоимость товаров в заказе с id = {}", orderDto.getOrderId());
        Map<UUID, Long> products = orderDto.getProducts();

        Map<UUID, Float> productPrices = products.keySet().stream()
                .map(storeClient::getProductById)
                .collect(Collectors.toMap(ProductDto::getProductId, ProductDto::getPrice));

        return products.entrySet().stream()
                .map(entry -> entry.getValue() * productPrices.get(entry.getKey()))
                .mapToDouble(Float::floatValue)
                .sum();
    }

    @Override
    @Transactional
    public void processFailedPayment(UUID paymentId) {
        log.info("Обрабатываем неуспешный платеж с id = {}", paymentId);
        Payment payment = findPayment(paymentId);
        orderClient.paymentFailed(payment.getOrderId());
        payment.setPaymentStatus(PaymentStatus.FAILED);
        paymentRepository.save(payment);
    }

    private void checkCosts(OrderDto orderDto) {
        if (orderDto.getTotalPrice() == null)
            throw new NotEnoughInfoInOrderToCalculateException("Отсутствует информация о стоимости заказа");
        if (orderDto.getDeliveryPrice() == null)
            throw new NotEnoughInfoInOrderToCalculateException("Отсутствует информация о стоимости доставки");
        if (orderDto.getProductPrice() == null)
            throw new NotEnoughInfoInOrderToCalculateException("Отсутствует информация о стоимости товаров");
    }

    private Payment findPayment(UUID paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NotFoundException("Платеж с id = " + paymentId + " не найден"));
    }
}