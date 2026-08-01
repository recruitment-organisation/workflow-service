package recruitment.dev.workflowservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StartRecruitmentRequest {

    @NotNull(message = "applicationId is required")
    @Positive(message = "applicationId must be positive")
    private Long applicationId;

    @NotNull(message = "candidateId is required")
    @Positive(message = "candidateId must be positive")
    private Long candidateId;

    @NotNull(message = "jobId is required")
    @Positive(message = "jobId must be positive")
    private Long jobId;

    @NotNull(message = "cvId is required")
    @Positive(message = "cvId must be positive")
    private Long cvId;
}