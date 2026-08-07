package recruitment.dev.workflowservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import recruitment.dev.workflowservice.dto.CvTemplateValidationResult;
import recruitment.dev.workflowservice.dto.application.ApplicationResponse;
import recruitment.dev.workflowservice.dto.application.UpdateApplicationWorkflowStateRequest;
import recruitment.dev.workflowservice.dto.application.UpdateMatchingScoreRequest;
import recruitment.dev.workflowservice.security.FeignErrorConfig;
import recruitment.dev.workflowservice.security.FeignSecurityConfig;
//eeeee
@FeignClient(
        name = "application-service",
        configuration = {FeignSecurityConfig.class, FeignErrorConfig.class},
        fallbackFactory = ApplicationClientFallbackFactory.class
)
public interface ApplicationClient {

    @GetMapping("/validation/cv/{applicationId}")
    CvTemplateValidationResult validateCvTemplate(
            @PathVariable("applicationId") Long applicationId
    );

    @PutMapping("/internal/update-status/{applicationId}")
    ApplicationResponse updateStatus(
            @PathVariable("applicationId") Long applicationId,
            @RequestBody String status
    );

    @PutMapping("/internal/applications/{applicationId}/matching-score")
    ApplicationResponse updateMatchingScore(
            @PathVariable("applicationId") Long applicationId,
            @RequestBody UpdateMatchingScoreRequest request
    );

    @PutMapping("/internal/applications/{applicationId}/workflow-state")
    void updateWorkflowState(
            @PathVariable("applicationId") Long applicationId,
            @RequestBody UpdateApplicationWorkflowStateRequest request
    );
}
