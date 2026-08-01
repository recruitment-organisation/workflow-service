package recruitment.dev.workflowservice.delegate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;
import recruitment.dev.workflowservice.client.ApplicationClient;
import recruitment.dev.workflowservice.client.RagClient;
import recruitment.dev.workflowservice.dto.application.ApplicationStatus;
import recruitment.dev.workflowservice.dto.rag.AnalyzeCvResponse;

import static recruitment.dev.workflowservice.util.WorkflowVariables.requiredLong;

@Slf4j
@Component("aiCvAnalysisDelegate")
@RequiredArgsConstructor
public class AiCvAnalysisDelegate implements JavaDelegate {

    private final RagClient ragClient;
    private final ApplicationClient applicationClient;

    @Override
    public void execute(DelegateExecution execution) {
        Long applicationId = requiredLong(execution, "applicationId");
        requiredLong(execution, "candidateId");
        requiredLong(execution, "jobId");
        requiredLong(execution, "cvId");

        applicationClient.updateStatus(
                applicationId,
                ApplicationStatus.UNDER_AI_REVIEW.name()
        );

        AnalyzeCvResponse response = ragClient.analyzeCv(applicationId);

        if (response == null || response.score() == null) {
            throw new IllegalStateException("RAG service returned no score");
        }

        double score = Math.max(0.0, Math.min(100.0, response.score()));

        execution.setVariable("aiScore", score);
        execution.setVariable("aiSummary", response.summary());
        execution.setVariable("aiRecommended", response.recommended());
        execution.setVariable("applicationStatus", ApplicationStatus.UNDER_AI_REVIEW.name());

        log.info("AI analysis finished: applicationId={}, score={}", applicationId, score);
    }
}
