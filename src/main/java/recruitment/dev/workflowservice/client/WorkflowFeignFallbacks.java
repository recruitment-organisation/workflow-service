package recruitment.dev.workflowservice.client;

import org.springframework.http.HttpStatus;
import recruitment.dev.workflowservice.exception.WorkflowExternalServiceException;

final class WorkflowFeignFallbacks {

    private WorkflowFeignFallbacks() {
    }

    static WorkflowExternalServiceException unavailable(String dependency, Throwable cause) {
        if (cause instanceof WorkflowExternalServiceException exception) {
            return exception;
        }
        return new WorkflowExternalServiceException(
                dependency,
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                "Dependency is unavailable",
                cause
        );
    }
}
