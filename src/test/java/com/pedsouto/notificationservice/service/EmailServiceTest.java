package com.pedsouto.notificationservice.service;

import com.pedsouto.notificationservice.dto.TaskDto;
import com.pedsouto.notificationservice.infra.enums.NotificationStatus;
import com.pedsouto.notificationservice.infra.exception.EmailException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private TemplateEngine templateEngine;

    @InjectMocks
    private EmailService emailService;

    private MimeMessage mimeMessage;

    @BeforeEach
    void setUp() {
        mimeMessage = new MimeMessage(Session.getInstance(new Properties()));
        ReflectionTestUtils.setField(emailService, "senderEmail", "no-reply@example.com");
        ReflectionTestUtils.setField(emailService, "senderName", "Task Scheduler");
    }

    @Test
    void shouldSendEmailUsingTaskData() throws Exception {
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(eq("notification"), any(Context.class))).thenReturn("<html>Task</html>");

        emailService.sendEmail(validTask());

        verify(javaMailSender).send(mimeMessage);
        assertEquals("Task Notification", mimeMessage.getSubject());
        assertEquals("user@example.com", mimeMessage.getAllRecipients()[0].toString());
    }

    @Test
    void shouldWrapSmtpFailureInEmailException() {
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(eq("notification"), any(Context.class))).thenReturn("<html>Task</html>");
        doThrow(new MailSendException("SMTP unavailable")).when(javaMailSender).send(mimeMessage);

        assertThrows(EmailException.class, () -> emailService.sendEmail(validTask()));
    }

    private TaskDto validTask() {
        return new TaskDto(
                "task-123",
                "Test task",
                "Task description",
                LocalDateTime.of(2026, 11, 5, 8, 0),
                LocalDateTime.of(2026, 11, 6, 10, 30),
                "user@example.com",
                null,
                NotificationStatus.PENDING
        );
    }
}
