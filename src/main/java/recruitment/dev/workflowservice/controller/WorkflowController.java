package recruitment.dev.workflowservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import recruitment.dev.workflowservice.dto.StartRecruitmentRequest;
import recruitment.dev.workflowservice.dto.StartRecruitmentResponse;
import recruitment.dev.workflowservice.service.WorkflowService;

@RestController
@RequestMapping("/workflow")
@RequiredArgsConstructor
public class WorkflowController {

    private final WorkflowService workflowService;

    @PostMapping("/start")
    public ResponseEntity<StartRecruitmentResponse> startRecruitment(
            @Valid @RequestBody StartRecruitmentRequest request
    ) {
        StartRecruitmentResponse response =
                workflowService.startRecruitment(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}