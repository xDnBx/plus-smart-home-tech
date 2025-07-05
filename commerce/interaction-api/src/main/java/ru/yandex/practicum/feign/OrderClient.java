package ru.yandex.practicum.feign;

import feign.FeignException;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.dto.order.OrderDto;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "order", path = "/api/v1/order")
public interface OrderClient {
    @GetMapping
    List<OrderDto> getClientOrders(@RequestParam String username) throws FeignException;

    @PutMapping
    OrderDto createNewOrder(@Valid @RequestBody CreateNewOrderRequest request) throws FeignException;

    @PostMapping("/completed")
    OrderDto completeOrder(@RequestBody UUID orderId) throws FeignException;

    @PostMapping("/delivery/failed")
    OrderDto deliveryFailed(@RequestBody UUID orderId) throws FeignException;

    @PostMapping("/assembly")
    OrderDto assembly(@RequestBody UUID orderId) throws FeignException;
}