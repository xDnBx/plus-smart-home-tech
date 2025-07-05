package ru.yandex.practicum.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.delivery.enums.DeliveryState;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.warehouse.AddressDto;
import ru.yandex.practicum.dto.warehouse.ShippedToDeliveryRequest;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.feign.OrderClient;
import ru.yandex.practicum.feign.WarehouseClient;
import ru.yandex.practicum.mapper.DeliveryMapper;
import ru.yandex.practicum.model.Delivery;
import ru.yandex.practicum.repository.DeliveryRepository;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeliveryServiceImpl implements DeliveryService {
    final DeliveryRepository deliveryRepository;
    final DeliveryMapper deliveryMapper;
    final WarehouseClient warehouseClient;
    final OrderClient orderClient;

    @Value("${delivery.base_rate}")
    Double baseRate;

    @Value("${delivery.address_rate}")
    Integer addressRate;

    @Value("${delivery.fragile_rate}")
    Double fragileRate;

    @Value("${delivery.weight_rate}")
    Double weightRate;

    @Value("${delivery.volume_rate}")
    Double volumeRate;

    @Value("${delivery.street_rate}")
    Double streetRate;

    @Override
    @Transactional
    public DeliveryDto planDelivery(DeliveryDto deliveryDto) {
        log.info("Планирование доставки для заказа c id = {}", deliveryDto.getOrderId());
        Delivery delivery = deliveryMapper.toEntity(deliveryDto);
        delivery.setDeliveryState(DeliveryState.CREATED);
        return deliveryMapper.toDto(deliveryRepository.save(delivery));
    }

    @Override
    @Transactional
    public void markAsSuccessful(UUID orderId) {
        log.info("Завершение доставки для заказа c id = {}", orderId);
        Delivery delivery = findDelivery(orderId);
        orderClient.completeOrder(delivery.getOrderId());
        delivery.setDeliveryState(DeliveryState.DELIVERED);
        deliveryRepository.save(delivery);
        log.info("Доставка для заказа c id = {} успешно завершена", orderId);
    }

    @Override
    @Transactional
    public void markAsPicked(UUID orderId) {
        log.info("Отправляем заказ c id = {} на сборку", orderId);
        Delivery delivery = findDelivery(orderId);
        orderClient.assembly(delivery.getOrderId());
        log.info("Заказ c id = {} отправлен на сборку", orderId);
        ShippedToDeliveryRequest request = ShippedToDeliveryRequest.builder()
                .orderId(delivery.getOrderId())
                .deliveryId(delivery.getDeliveryId())
                .build();
        warehouseClient.shipToDelivery(request);
        log.info("Заказ c id = {} отправлен на склад для доставки", orderId);
        delivery.setDeliveryState(DeliveryState.IN_PROGRESS);
        deliveryRepository.save(delivery);
        log.info("Заказ c id = {} собран", orderId);
    }

    @Override
    @Transactional
    public void markAsFailed(UUID orderId) {
        log.info("Проставление статуса заказа c id = {} как не доставлен", orderId);
        Delivery delivery = findDelivery(orderId);
        orderClient.deliveryFailed(delivery.getOrderId());
        delivery.setDeliveryState(DeliveryState.FAILED);
        deliveryRepository.save(delivery);
        log.info("Проставлен статус заказа c id = {} как не доставлен", orderId);
    }

    @Override
    @Transactional(readOnly = true)
    public Double calculateDeliveryCost(OrderDto orderDto) {
        log.info("Расчет итоговой стоимости доставки для заказа c id = {}", orderDto.getOrderId());
        Delivery delivery = findDelivery(orderDto.getDeliveryId());
        AddressDto address = warehouseClient.getWarehouseAddress();
        Double deliveryCost = baseRate;

        if (address.getStreet().equals("ADDRESS_1")) {
            deliveryCost += deliveryCost;
        } else if (address.getStreet().equals("ADDRESS_2")) {
            deliveryCost += deliveryCost * addressRate;
        }

        if (orderDto.isFragile()) deliveryCost += deliveryCost * fragileRate;

        deliveryCost += orderDto.getDeliveryWeight() * weightRate;
        deliveryCost += orderDto.getDeliveryVolume() * volumeRate;

        if (delivery.getToAddress().getStreet().equals(address.getStreet())) deliveryCost += deliveryCost * streetRate;

        log.info("Расчет итоговой стоимости доставки для заказа c id = {} завершен", orderDto.getOrderId());
        return deliveryCost;
    }

    private Delivery findDelivery(UUID deliveryId) {
        return deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new NotFoundException("Доставка с id = " + deliveryId + " не найдена"));
    }
}