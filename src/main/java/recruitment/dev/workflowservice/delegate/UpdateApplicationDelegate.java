package recruitment.dev.workflowservice.delegate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;
import recruitment.dev.workflowservice.client.ApplicationClient;
import recruitment.dev.workflowservice.dto.application.ApplicationStatus;

import static recruitment.dev.workflowservice.util.WorkflowVariables.*;

@Slf4j
@Component("updateApplicationDelegate")
@RequiredArgsConstructor
public class UpdateApplicationDelegate implements JavaDelegate {

    private final ApplicationClient applicationClient;

    @Override
    public void execute(DelegateExecution execution) {
        Long applicationId = requiredLong(execution, "applicationId");
        String reason = determineReason(execution);

        applicationClient.updateStatus(
                applicationId,
                ApplicationStatus.REJECTED.name()
        );

        execution.setVariable("applicationStatus", ApplicationStatus.REJECTED.name());
        execution.setVariable("rejectionReason", reason);

        log.info("Application rejected: applicationId={}, reason={}", applicationId, reason);
    }

    private String determineReason(DelegateExecution execution) {
        if (!booleanValue(execution, "hrCvApproved", true)) {
            return combine("HR_CV_FILTER_REJECTED", optionalString(execution, "hrCvComment"));
        }
        if (!booleanValue(execution, "hrApproved", true)) {
            return combine("HR_REJECTED", optionalString(execution, "hrComment"));
        }
        if (!booleanValue(execution, "technicalApproved", true)) {
            return combine("TECHNICAL_REJECTED", optionalString(execution, "technicalComment"));
        }
        if (!booleanValue(execution, "managerApproved", true)) {
            return combine("MANAGER_REJECTED", optionalString(execution, "managerComment"));
        }
        return "REJECTED_BY_WORKFLOW";
    }

    private String combine(String code, String comment) {
        return comment == null || comment.isBlank() ? code : code + ": " + comment;
    }
}
