package ru.yandex.practicum.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Embeddable
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Dimension {
    @Column(name = "width", nullable = false)
    Double width;

    @Column(name = "height", nullable = false)
    Double height;

    @Column(name = "depth", nullable = false)
    Double depth;
}