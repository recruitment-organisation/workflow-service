package recruitment.dev.workflowservice.dto;


import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class WorkflowTaskResponse {

    private String taskId;
    private String taskDefinitionKey;
    private String taskName;

    private String processInstanceId;
    private String processDefinitionId;

    private String assignee;
    private LocalDateTime createdAt;
}