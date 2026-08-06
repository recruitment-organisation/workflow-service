package recruitment.dev.workflowservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.TaskService;
import org.flowable.task.api.Task;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import recruitment.dev.workflowservice.dto.StartRecruitmentRequest;
import recruitment.dev.workflowservice.dto.StartRecruitmentResponse;
import recruitment.dev.workflowservice.dto.application.ApplicationStatus;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowServiceImpl implements WorkflowService {

    private static final String PROCESS_KEY = "recruitmentProcess";

    private final RuntimeService runtimeService;
    private final TaskService taskService;

    @Override
    @Transactional
    public StartRecruitmentResponse startRecruitment(
            StartRecruitmentRequest request
    ) {
        String businessKey =
                String.valueOf(request.getApplicationId());

        verifyNoActiveProcess(businessKey);

        Map<String, Object> variables = new HashMap<>();

        variables.put("applicationId", request.getApplicationId());
        variables.put("candidateId", request.getCandidateId());
        variables.put("jobId", request.getJobId());
        variables.put("cvId", request.getCvId());

        variables.put("applicationSubmitted", true);
        variables.put("applicationStatus", "SUBMITTED");

        ProcessInstance processInstance =
                runtimeService.startProcessInstanceByKey(
                        PROCESS_KEY,
                        businessKey,
                        variables
                );

        Task currentTask = taskService
                .createTaskQuery()
                .processInstanceId(processInstance.getProcessInstanceId())
                .active()
                .orderByTaskCreateTime()
                .asc()
                .list()
                .stream()
                .findFirst()
                .orElse(null);

        log.info(
                "Recruitment workflow started: " +
                        "processInstanceId={}, applicationId={}",
                processInstance.getProcessInstanceId(),
                request.getApplicationId()
        );

        return StartRecruitmentResponse.builder()
                .processInstanceId(
                        processInstance.getProcessInstanceId()
                )
                .processDefinitionId(
                        processInstance.getProcessDefinitionId()
                )
                .businessKey(processInstance.getBusinessKey())
                .currentStatus(statusFor(currentTask).name())
                .currentTaskId(currentTask == null ? null : currentTask.getId())
                .currentTaskDefinitionKey(currentTask == null ? null : currentTask.getTaskDefinitionKey())
                .currentTaskName(currentTask == null ? null : currentTask.getName())
                .build();
    }

    private ApplicationStatus statusFor(Task task) {
        if (task != null && "sid-3140788F-868D-4F20-88A1-D66AF0BA345A".equals(task.getTaskDefinitionKey())) {
            return ApplicationStatus.CV_REVISION_REQUIRED;
        }
        return ApplicationStatus.SUBMITTED;
    }

    private void verifyNoActiveProcess(String businessKey) {
        long activeProcessCount =
                runtimeService
                        .createProcessInstanceQuery()
                        .processInstanceBusinessKey(businessKey)
                        .active()
                        .count();

        if (activeProcessCount > 0) {
            throw new IllegalStateException(
                    "An active workflow already exists for " +
                            "applicationId=" + businessKey
            );
        }
    }
}
