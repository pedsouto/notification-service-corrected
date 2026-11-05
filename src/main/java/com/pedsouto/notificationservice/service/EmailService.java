package com.pedsouto.notificationservice.service;

import com.pedsouto.notificationservice.dto.TaskDto;
import com.pedsouto.notificationservice.infra.exception.EmailException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Value("${email.send.sender-email}")
    private String senderEmail;

    @Value("${email.send.sender-name}")
    private String senderName;

    public void sendEmail(TaskDto taskDto) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());

            mimeMessageHelper.setFrom(new InternetAddress(senderEmail, senderName));
            mimeMessageHelper.setTo(InternetAddress.parse(taskDto.userEmail()));
            mimeMessageHelper.setSubject("Task Notification");

            Context context = new Context();
            context.setVariable("taskTitle", taskDto.title());
            context.setVariable("eventDate", taskDto.eventDate());
            context.setVariable("description", taskDto.description());

            String template = templateEngine.process("notification", context);
            mimeMessageHelper.setText(template, true);

            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            throw new EmailException("Failed to process or send email", e);
        }
    }
}
