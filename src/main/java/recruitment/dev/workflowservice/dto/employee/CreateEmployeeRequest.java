package recruitment.dev.workflowservice.dto.employee;

import java.time.LocalDate;

public record CreateEmployeeRequest(
        String keycloakId,
        String firstName,
        String lastName,
        String email,
        String phone,
        LocalDate hireDate,
        String position,
        Long departmentId,
        Long roleId
) {
}
