package recruitment.dev.workflowservice.dto.employee;

public record EmployeeResponse(
        Long id,
        String keycloakId,
        String firstName,
        String lastName,
        String email,
        String position
) {
}
