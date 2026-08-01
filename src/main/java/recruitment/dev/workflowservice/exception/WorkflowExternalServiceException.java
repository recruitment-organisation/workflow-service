package recruitment.dev.workflowservice.exception;

public class WorkflowExternalServiceException extends RuntimeException {

    private final String methodKey;
    private final int status;

    public WorkflowExternalServiceException(String methodKey, int status, String reason) {
        super("External service call failed [" + methodKey + "] with status " + status + ": " + reason);
        this.methodKey = methodKey;
        this.status = status;
    }

    public WorkflowExternalServiceException(String methodKey, int status, String reason, Throwable cause) {
        super("External service call failed [" + methodKey + "] with status " + status + ": " + reason, cause);
        this.methodKey = methodKey;
        this.status = status;
    }

    public String getMethodKey() {
        return methodKey;
    }

    public int getStatus() {
        return status;
    }
}
