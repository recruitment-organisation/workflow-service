package recruitment.dev.workflowservice.dto.auth;

public record UpdateUserRoleRequest(
        String removeRole,
        String addRole
) {
}
