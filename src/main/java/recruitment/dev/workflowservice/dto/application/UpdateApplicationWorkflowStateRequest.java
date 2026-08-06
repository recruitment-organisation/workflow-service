package recruitment.dev.workflowservice.dto.application;

public record UpdateApplicationWorkflowStateRequest(
        String processInstanceId,
        String currentTaskId,
        String currentTaskDefinitionKey,
        String currentTaskName
) {
}
