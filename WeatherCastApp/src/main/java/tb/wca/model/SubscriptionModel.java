package tb.wca.model;

import jakarta.persistence.*;
import tb.wca.entity.CityEntity;
import tb.wca.entity.UserEntity;

import java.time.Instant;
import java.time.LocalTime;

public record SubscriptionModel(
        Long id,
        LocalTime notificationTime,
        String timeZone,
        Boolean isActive,
        Instant createdAt,
        Instant updatedAt
) { }
