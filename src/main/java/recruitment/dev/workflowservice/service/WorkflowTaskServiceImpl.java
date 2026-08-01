package recruitment.dev.workflowservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.TaskService;
import org.flowable.task.api.Task;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import recruitment.dev.workflowservice.dto.CompleteTaskRequest;
import recruitment.dev.workflowservice.dto.WorkflowTaskResponse;
import recruitment.dev.workflowservice.exception.WorkflowTaskNotFoundException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowTaskServiceImpl implements WorkflowTaskService {

    private final TaskService taskService;

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