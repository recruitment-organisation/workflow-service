package recruitment.dev.workflowservice.dto.candidate;

public record CandidateResponse(
        Long id,
        String keycloakId,
        String firstName,
        String lastName,
        String email,
        String phone,
        String location
) {
}
