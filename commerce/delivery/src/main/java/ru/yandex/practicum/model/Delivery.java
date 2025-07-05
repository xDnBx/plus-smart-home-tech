package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.dto.delivery.enums.DeliveryState;

import java.util.UUID;

@Entity
@Table(name = "deliveries")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "delivery_id")
    UUID deliveryId;

    @OneToOne
    @JoinColumn(name = "from_address_id")
    Address fromAddress;

    @OneToOne
    @JoinColumn(name = "to_address_id")
    Address toAddress;

    @Column(name = "order_id")
    UUID orderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_state")
    DeliveryState deliveryState;
}