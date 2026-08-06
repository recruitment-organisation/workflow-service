package recruitment.dev.workflowservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.TaskService;
import org.flowable.task.api.Task;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import recruitment.dev.workflowservice.client.ApplicationClient;
import recruitment.dev.workflowservice.dto.CompleteTaskRequest;
import recruitment.dev.workflowservice.dto.WorkflowTaskResponse;
import recruitment.dev.workflowservice.dto.application.UpdateApplicationWorkflowStateRequest;
import recruitment.dev.workflowservice.dto.application.ApplicationStatus;
import recruitment.dev.workflowservice.exception.WorkflowTaskNotFoundException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowTaskServiceImpl implements WorkflowTaskService {

    private final TaskService taskService;
    private final ApplicationClient applicationClient;

    @Override
    @Transactional(readOnly = true)
    public List<WorkflowTaskResponse> getTasksByProcessInstanceId(
            String processInstanceId
    ) {
        return taskService
                .createTaskQuery()
                .processInstanceId(processInstanceId)
                .active()
                .orderByTaskCreateTime()
                .asc()
                .list()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public WorkflowTaskResponse getTaskById(String taskId) {
        Task task = findTaskById(taskId);
        return toResponse(task);
    }

    @Override
    @Transactional
    public void completeTask(CompleteTaskRequest request) {
        Task task = findTaskById(request.getTaskId());
        Long applicationId = applicationIdFor(task);

        log.info(
                "Completing workflow task: taskId={}, taskName={}, " +
                        "processInstanceId={}, variables={}",
                task.getId(),
                task.getName(),
                task.getProcessInstanceId(),
                request.getVariables()
        );

        taskService.complete(
                task.getId(),
                request.getVariables()
        );

        if (applicationId != null) {
            synchronizeCurrentTask(applicationId, task.getProcessInstanceId());
        }

        log.info(
                "Workflow task completed: taskId={}, taskName={}",
                task.getId(),
                task.getName()
        );
    }

    private Task findTaskById(String taskId) {
        Task task = taskService
                .createTaskQuery()
                .taskId(taskId)
                .active()
                .singleResult();

        if (task == null) {
            throw new WorkflowTaskNotFoundException(
                    "Active workflow task not found: " + taskId
            );
        }

        return task;
    }

    private Long applicationIdFor(Task task) {
        Object value = taskService.getVariable(task.getId(), "applicationId");
        if (value instanceof Number number) {
            return number.longValue();
        }
        if (value instanceof String string) {
            try {
                return Long.valueOf(string);
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    private void synchronizeCurrentTask(Long applicationId, String processInstanceId) {
        Task currentTask = taskService
                .createTaskQuery()
                .processInstanceId(processInstanceId)
                .active()
                .orderByTaskCreateTime()
                .asc()
                .list()
                .stream()
                .findFirst()
                .orElse(null);

        applicationClient.updateWorkflowState(
                applicationId,
                new UpdateApplicationWorkflowStateRequest(
                        processInstanceId,
                        currentTask == null ? null : currentTask.getId(),
                        currentTask == null ? null : currentTask.getTaskDefinitionKey(),
                        currentTask == null ? null : currentTask.getName()
                )
        );

        if (currentTask != null) {
            ApplicationStatus nextStatus = switch (currentTask.getTaskDefinitionKey()) {
                case "sid-3140788F-868D-4F20-88A1-D66AF0BA345A" -> ApplicationStatus.CV_REVISION_REQUIRED;
                case "hrCvFiltering" -> ApplicationStatus.SUBMITTED;
                case "hrInterview" -> ApplicationStatus.HR_INTERVIEW;
                case "technicalInterview" -> ApplicationStatus.TECHNICAL_INTERVIEW;
                case "managerInterview" -> ApplicationStatus.MANAGER_INTERVIEW;
                default -> null;
            };
            if (nextStatus != null) {
                applicationClient.updateStatus(applicationId, nextStatus.name());
            }
        }
    }

    private WorkflowTaskResponse toResponse(Task task) {
        return WorkflowTaskResponse.builder()
                .taskId(task.getId())
                .taskDefinitionKey(task.getTaskDefinitionKey())
                .taskName(task.getName())
                .processInstanceId(task.getProcessInstanceId())
                .processDefinitionId(task.getProcessDefinitionId())
                .assignee(task.getAssignee())
                .createdAt(convertDate(task))
                .build();
    }

    private LocalDateTime convertDate(Task task) {
        if (task.getCreateTime() == null) {
            return null;
        }

        return task.getCreateTime()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}
