package recruitment.dev.workflowservice.delegate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;
import recruitment.dev.workflowservice.client.ApplicationClient;
import recruitment.dev.workflowservice.dto.application.ApplicationStatus;

import static recruitment.dev.workflowservice.util.WorkflowVariables.requiredLong;

@Slf4j
@Component("updateApplicationStatusDelegate")
@RequiredArgsConstructor
public class UpdateApplicationStatusDelegate implements JavaDelegate {

    private final ApplicationClient applicationClient;

    @Override
    public void execute(DelegateExecution execution) {
        Long applicationId = requiredLong(execution, "applicationId");
        String reason = "CV correction deadline exceeded";

        applicationClient.updateStatus(
                applicationId,
                ApplicationStatus.CLOSED.name()
        );

        execution.setVariable("applicationStatus", ApplicationStatus.CLOSED.name());
        execution.setVariable("rejectionReason", reason);

        log.info("Application closed after timeout: applicationId={}", applicationId);
    }
}
