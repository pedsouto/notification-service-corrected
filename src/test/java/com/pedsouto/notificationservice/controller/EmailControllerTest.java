package com.pedsouto.notificationservice.controller;

import com.pedsouto.notificationservice.infra.security.JwtUtil;
import com.pedsouto.notificationservice.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmailController.class)
@Import({com.pedsouto.notificationservice.infra.security.SecurityConfig.class, GlobalExceptionHandler.class})
class EmailControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        when(jwtUtil.extractUsername("valid-token")).thenReturn("user@example.com");
        doNothing().when(emailService).sendEmail(any());
    }

    @Test
    void shouldRejectRequestWithoutJwt() throws Exception {
        mockMvc.perform(post("/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validPayload()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldSendEmailForAuthenticatedOwner() throws Exception {
        mockMvc.perform(post("/email")
                        .header("Authorization", "Bearer valid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validPayload()))
                .andExpect(status().isNoContent());

        verify(emailService).sendEmail(any());
    }

    @Test
    void shouldRejectSendingEmailToAnotherUser() throws Exception {
        String payload = validPayload().replace("user@example.com", "other@example.com");

        mockMvc.perform(post("/email")
                        .header("Authorization", "Bearer valid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldRejectInvalidPayload() throws Exception {
        mockMvc.perform(post("/email")
                        .header("Authorization", "Bearer valid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"\",\"title\":\"\",\"eventDate\":null,\"userEmail\":\"invalid\"}"))
                .andExpect(status().isBadRequest());
    }

    private String validPayload() {
        return """
                {
                  "id": "task-123",
                  "title": "Test task",
                  "description": "Task description",
                  "createdAt": "05-11-2026 08:00:00",
                  "eventDate": "06-11-2026 10:30:00",
                  "userEmail": "user@example.com",
                  "updatedAt": null,
                  "notificationStatus": "PENDING"
                }
                """;
    }
}
