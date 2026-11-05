package com.pedsouto.notificationservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pedsouto.notificationservice.infra.enums.NotificationStatus;

import java.time.LocalDateTime;

public record TaskDto(
        String id,
        String title,
        String description,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
        LocalDateTime createdAt,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
        LocalDateTime eventDate,

        String userEmail,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
        LocalDateTime updatedAt,

        NotificationStatus notificationStatus
) {
}
