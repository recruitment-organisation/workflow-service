package recruitment.dev.workflowservice.service;

import org.flowable.engine.TaskService;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import recruitment.dev.workflowservice.dto.CompleteTaskRequest;
import recruitment.dev.workflowservice.dto.WorkflowTaskResponse;
import recruitment.dev.workflowservice.exception.WorkflowTaskNotFoundException;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkflowTaskServiceImplTest {

    @Mock private TaskService taskService;
    @Mock private TaskQuery taskQuery;
    @InjectMocks private WorkflowTaskServiceImpl service;

    @Test
    void returnsMappedActiveTasksForProcess() {
        Task task = task("task-1");
        when(taskService.createTaskQuery()).thenReturn(taskQuery);
        when(taskQuery.processInstanceId("process-1")).thenReturn(taskQuery);
        when(taskQuery.active()).thenReturn(taskQuery);
        when(taskQuery.orderByTaskCreateTime()).thenReturn(taskQuery);
        when(taskQuery.asc()).thenReturn(taskQuery);
        when(taskQuery.list()).thenReturn(List.of(task));

        List<WorkflowTaskResponse> tasks = service.getTasksByProcessInstanceId("process-1");

        assertThat(tasks).singleElement().satisfies(response -> {
            assertThat(response.getTaskId()).isEqualTo("task-1");
            assertThat(response.getTaskName()).isEqualTo("Review CV");
            assertThat(response.getCreatedAt()).isNotNull();
        });
    }

    @Test
    void completesFoundTaskWithRequestVariables() {
        Task task = taskForCompletion("task-2");
        CompleteTaskRequest request = new CompleteTaskRequest();
        request.setTaskId("task-2");
        request.setVariables(Map.of("approved", true));
        when(taskService.createTaskQuery()).thenReturn(taskQuery);
        when(taskQuery.taskId("task-2")).thenReturn(taskQuery);
        when(taskQuery.active()).thenReturn(taskQuery);
        when(taskQuery.singleResult()).thenReturn(task);

        service.completeTask(request);

        verify(taskService).complete("task-2", request.getVariables());
    }

    @Test
    void reportsUnknownTask() {
        when(taskService.createTaskQuery()).thenReturn(taskQuery);
        when(taskQuery.taskId("missing")).thenReturn(taskQuery);
        when(taskQuery.active()).thenReturn(taskQuery);
        when(taskQuery.singleResult()).thenReturn(null);

        assertThatThrownBy(() -> service.getTaskById("missing"))
                .isInstanceOf(WorkflowTaskNotFoundException.class)
                .hasMessageContaining("missing");
    }

    private Task task(String id) {
        Task task = mock(Task.class);
        when(task.getId()).thenReturn(id);
        when(task.getTaskDefinitionKey()).thenReturn("reviewCv");
        when(task.getName()).thenReturn("Review CV");
        when(task.getProcessInstanceId()).thenReturn("process-1");
        when(task.getProcessDefinitionId()).thenReturn("recruitment:1:1");
        when(task.getAssignee()).thenReturn("hr-user");
        when(task.getCreateTime()).thenReturn(Date.from(Instant.parse("2026-01-01T10:00:00Z")));
        return task;
    }

    private Task taskForCompletion(String id) {
        Task task = mock(Task.class);
        when(task.getId()).thenReturn(id);
        when(task.getName()).thenReturn("Review CV");
        when(task.getProcessInstanceId()).thenReturn("process-1");
        return task;
    }
}
