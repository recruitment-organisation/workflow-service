package recruitment.dev.workflowservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import recruitment.dev.workflowservice.dto.CompleteTaskRequest;
import recruitment.dev.workflowservice.dto.WorkflowTaskResponse;
import recruitment.dev.workflowservice.service.WorkflowTaskService;

import java.util.List;

@RestController
@RequestMapping("/workflow-tasks")
@RequiredArgsConstructor
public class WorkflowTaskController {

    private final WorkflowTaskService workflowTaskService;

    @GetMapping("/process/{processInstanceId}")
    public ResponseEntity<List<WorkflowTaskResponse>>
    getTasksByProcessInstanceId(
            @PathVariable String processInstanceId
    ) {
        return ResponseEntity.ok(
                workflowTaskService.getTasksByProcessInstanceId(
                        processInstanceId
                )
        );
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<WorkflowTaskResponse> getTaskById(
            @PathVariable String taskId
    ) {
        return ResponseEntity.ok(
                workflowTaskService.getTaskById(taskId)
        );
    }

    @PostMapping("/complete")
    public ResponseEntity<Void> completeTask(
            @Valid @RequestBody CompleteTaskRequest request
    ) {
        workflowTaskService.completeTask(request);

        return ResponseEntity.noContent().build();
    }
}
