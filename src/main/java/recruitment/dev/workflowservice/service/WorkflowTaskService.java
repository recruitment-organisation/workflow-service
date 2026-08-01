package recruitment.dev.workflowservice.service;

import recruitment.dev.workflowservice.dto.CompleteTaskRequest;
import recruitment.dev.workflowservice.dto.WorkflowTaskResponse;

import java.util.List;

public interface WorkflowTaskService {

    List<WorkflowTaskResponse> getTasksByProcessInstanceId(
            String processInstanceId
    );

    WorkflowTaskResponse getTaskById(String taskId);

    void completeTask(CompleteTaskRequest request);
}