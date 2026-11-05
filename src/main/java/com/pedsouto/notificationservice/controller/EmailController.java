package com.pedsouto.notificationservice.controller;

import com.pedsouto.notificationservice.dto.TaskDto;
import com.pedsouto.notificationservice.infra.enums.NotificationStatus;
import com.pedsouto.notificationservice.service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/email")
public class EmailController {

    private final EmailService emailService;

    @PostMapping
    public ResponseEntity<Void> sendEmail(
            @AuthenticationPrincipal String authenticatedEmail,
            @Valid @RequestBody TaskDto taskDto) {

        if (!authenticatedEmail.equalsIgnoreCase(taskDto.userEmail())) {
            throw new AccessDeniedException("O usuário autenticado não corresponde ao destinatário da notificação");
        }

        if (taskDto.notificationStatus() != NotificationStatus.PENDING) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Somente tarefas com notificação pendente podem gerar um e-mail"
            );
        }

        emailService.sendEmail(taskDto);
        return ResponseEntity.noContent().build();
    }
}
