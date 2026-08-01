package recruitment.dev.workflowservice.dto.application;

public record ApplicationResponse(
        Long id,
        Long candidateId,
        Long jobOfferId,
        Long cvId,
        ApplicationStatus status,
        Double matchingScore
) {
}
