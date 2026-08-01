package recruitment.dev.workflowservice.delegate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;
import recruitment.dev.workflowservice.client.CandidateClient;
import recruitment.dev.workflowservice.client.EmployeeClient;
import recruitment.dev.workflowservice.dto.candidate.CandidateResponse;
import recruitment.dev.workflowservice.dto.employee.CreateEmployeeRequest;
import recruitment.dev.workflowservice.dto.employee.EmployeeResponse;

import java.time.LocalDate;

import static recruitment.dev.workflowservice.util.WorkflowVariables.*;

@Slf4j
@Component("convertCandidateDelegate")
@RequiredArgsConstructor
public class ConvertCandidateDelegate implements JavaDelegate {

    private final CandidateClient candidateClient;
    private final EmployeeClient employeeClient;

    @Override
    public void execute(DelegateExecution execution) {
        Long candidateId = requiredLong(execution, "candidateId");
        CandidateResponse candidate = candidateClient.getCandidateById(candidateId);

        String position = optionalString(execution, "position");
        Long departmentId = optionalLong(execution, "departmentId");
        Long employeeRoleId = optionalLong(execution, "employeeRoleId");

        EmployeeResponse employee = employeeClient.createEmployee(
                new CreateEmployeeRequest(
                        candidate.keycloakId(),
                        candidate.firstName(),
                        candidate.lastName(),
                        candidate.email(),
                        candidate.phone(),
                        LocalDate.now(),
                        position == null || position.isBlank() ? "Employee" : position,
                        departmentId,
                        employeeRoleId
                )
        );

        if (employee == null || employee.id() == null) {
            throw new IllegalStateException("Employee service returned an invalid employee");
        }

        execution.setVariable("employeeId", employee.id());
        execution.setVariable("candidateKeycloakId", candidate.keycloakId());
        execution.setVariable("candidateEmail", candidate.email());
        execution.setVariable("employeeCreated", true);

        log.info("Candidate converted: candidateId={}, employeeId={}", candidateId, employee.id());
    }
}
