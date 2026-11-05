package com.pedsouto.notificationservice.controller;

import com.pedsouto.notificationservice.dto.TaskDto;
import com.pedsouto.notificationservice.service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

        emailService.sendEmail(taskDto);
        return ResponseEntity.noContent().build();
    }
}
