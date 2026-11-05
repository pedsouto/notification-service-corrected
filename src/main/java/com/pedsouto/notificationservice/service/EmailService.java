package com.pedsouto.notificationservice.service;

import com.pedsouto.notificationservice.dto.TaskDto;
import com.pedsouto.notificationservice.infra.exception.EmailException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private static final DateTimeFormatter EVENT_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy 'at' HH:mm");

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Value("${email.send.sender-email}")
    private String senderEmail;

    @Value("${email.send.sender-name}")
    private String senderName;

    public void sendEmail(TaskDto taskDto) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper =
                    new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());

            messageHelper.setFrom(new InternetAddress(senderEmail, senderName));
            messageHelper.setTo(new InternetAddress(taskDto.userEmail(), true));
            messageHelper.setSubject("Task Notification");

            Context context = new Context();
            context.setVariable("taskId", taskDto.id());
            context.setVariable("taskTitle", taskDto.title());
            context.setVariable("eventDate", taskDto.eventDate().format(EVENT_DATE_FORMATTER));
            context.setVariable("description", taskDto.description());

            String template = templateEngine.process("notification", context);
            messageHelper.setText(template, true);

            javaMailSender.send(mimeMessage);
        } catch (Exception ex) {
            log.error("Falha ao processar ou enviar notificação por e-mail", ex);
            throw new EmailException("Não foi possível processar ou enviar a notificação", ex);
        }
    }
}
