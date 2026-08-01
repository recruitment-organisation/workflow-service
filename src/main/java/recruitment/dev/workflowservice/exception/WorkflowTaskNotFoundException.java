package recruitment.dev.workflowservice.exception;

public class WorkflowTaskNotFoundException extends RuntimeException {

    public WorkflowTaskNotFoundException(String message) {
        super(message);
    }
}