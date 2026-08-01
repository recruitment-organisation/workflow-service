package recruitment.dev.workflowservice.client;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import recruitment.dev.workflowservice.dto.CvTemplateValidationResult;
import recruitment.dev.workflowservice.dto.application.ApplicationResponse;
import recruitment.dev.workflowservice.dto.application.UpdateMatchingScoreRequest;

@Component
public class ApplicationClientFallbackFactory implements FallbackFactory<ApplicationClient> {
    @Override public ApplicationClient create(Throwable cause) {
        return new ApplicationClient() {
            @Override public CvTemplateValidationResult validateCvTemplate(Long applicationId) { throw WorkflowFeignFallbacks.unavailable("application-service", cause); }
            @Override public ApplicationResponse updateStatus(Long applicationId, String status) { throw WorkflowFeignFallbacks.unavailable("application-service", cause); }
            @Override public ApplicationResponse updateMatchingScore(Long applicationId, UpdateMatchingScoreRequest request) { throw WorkflowFeignFallbacks.unavailable("application-service", cause); }
        };
    }
}
