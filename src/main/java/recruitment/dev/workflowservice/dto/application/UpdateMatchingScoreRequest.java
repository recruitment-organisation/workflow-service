package recruitment.dev.workflowservice.dto.application;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record UpdateMatchingScoreRequest(
        @NotNull
        @DecimalMin(value = "0.0")
        @DecimalMax(value = "100.0")
        Double matchingScore
) {
}
