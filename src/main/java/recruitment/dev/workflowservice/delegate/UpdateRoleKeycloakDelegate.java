package recruitment.dev.workflowservice.delegate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;
import recruitment.dev.workflowservice.client.AuthClient;
import recruitment.dev.workflowservice.dto.auth.UpdateUserRoleRequest;

import static recruitment.dev.workflowservice.util.WorkflowVariables.requiredString;

@Slf4j
@Component("updateRoleKeycloakDelegate")
@RequiredArgsConstructor
public class UpdateRoleKeycloakDelegate implements JavaDelegate {

    private final AuthClient authClient;

    @Override
    public void execute(DelegateExecution execution) {
        String keycloakId = requiredString(execution, "candidateKeycloakId");

        authClient.updateRole(
                keycloakId,
                new UpdateUserRoleRequest("ROLE_CANDIDATE", "ROLE_EMPLOYEE")
        );

        execution.setVariable("keycloakRoleUpdated", true);
        log.info("Keycloak role updated: keycloakId={}", keycloakId);
    }
}
