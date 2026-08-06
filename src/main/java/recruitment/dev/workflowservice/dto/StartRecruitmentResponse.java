package recruitment.dev.workflowservice.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StartRecruitmentResponse {

    private String processInstanceId;
    private String processDefinitionId;
    private String businessKey;
    private String currentStatus;

    private String currentTaskId;
    private String currentTaskDefinitionKey;
    private String currentTaskName;
}
