package recruitment.dev.workflowservice.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import recruitment.dev.workflowservice.controller.WorkflowController;
import recruitment.dev.workflowservice.dto.StartRecruitmentResponse;
import recruitment.dev.workflowservice.service.WorkflowService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WorkflowController.class)
@Import({SecurityConfig.class, JwtAuthConverter.class})
class WorkflowControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WorkflowService workflowService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    void rejectsARequestWithoutBearerToken() throws Exception {
        mockMvc.perform(post("/workflow/start")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequest()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void acceptsAValidatedJwt() throws Exception {
        StartRecruitmentResponse response = StartRecruitmentResponse.builder()
                .processInstanceId("process-1")
                .build();
        when(workflowService.startRecruitment(any())).thenReturn(response);

        mockMvc.perform(post("/workflow/start")
                        .with(jwt().jwt(token -> token
                                .subject("user-1")
                                .claim("preferred_username", "recruiter")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequest()))
                .andExpect(status().isCreated());
    }

    private String validRequest() {
        return """
                {
                  "applicationId": 10,
                  "candidateId": 20,
                  "jobId": 30,
                  "cvId": 40
                }
                """;
    }
}
