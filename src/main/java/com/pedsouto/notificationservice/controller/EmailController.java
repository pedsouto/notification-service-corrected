package com.pedsouto.notificationservice.controller;

import com.pedsouto.notificationservice.dto.TaskDto;
import com.pedsouto.notificationservice.service.EmailService;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<Void> sendEmail(@RequestBody TaskDto taskDto) {
        emailService.sendEmail(taskDto);
        return ResponseEntity.noContent().build();
    }
}
