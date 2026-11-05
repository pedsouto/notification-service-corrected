package com.pedsouto.notificationservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pedsouto.notificationservice.infra.enums.NotificationStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record TaskDto(
        @NotBlank(message = "O identificador da tarefa é obrigatório")
        String id,
        @NotBlank(message = "O título é obrigatório")
        @Size(max = 120, message = "O título deve ter no máximo 120 caracteres")
        String title,
        @Size(max = 2000, message = "A descrição deve ter no máximo 2000 caracteres")
        String description,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
        LocalDateTime createdAt,
        @NotNull(message = "A data do evento é obrigatória")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
        LocalDateTime eventDate,
        @NotBlank(message = "O e-mail do usuário é obrigatório")
        @Email(message = "O e-mail do usuário deve ser válido")
        @Size(max = 254, message = "O e-mail deve ter no máximo 254 caracteres")
        String userEmail,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
        LocalDateTime updatedAt,
        @NotNull(message = "O status da notificação é obrigatório")
        NotificationStatus notificationStatus
) {
}
