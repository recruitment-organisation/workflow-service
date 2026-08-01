package recruitment.dev.workflowservice.service;

import recruitment.dev.workflowservice.dto.StartRecruitmentRequest;
import recruitment.dev.workflowservice.dto.StartRecruitmentResponse;

public interface WorkflowService {

    StartRecruitmentResponse startRecruitment(
            StartRecruitmentRequest request
    );
}