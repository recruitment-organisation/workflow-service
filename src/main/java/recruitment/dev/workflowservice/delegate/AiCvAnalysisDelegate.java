package recruitment.dev.workflowservice.delegate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;
import recruitment.dev.workflowservice.client.ApplicationClient;
import recruitment.dev.workflowservice.client.RagClient;
import recruitment.dev.workflowservice.dto.application.ApplicationStatus;
import recruitment.dev.workflowservice.dto.application.UpdateMatchingScoreRequest;
import recruitment.dev.workflowservice.dto.rag.AnalyzeCvResponse;
import recruitment.dev.workflowservice.exception.WorkflowExternalServiceException;

import static recruitment.dev.workflowservice.util.WorkflowVariables.requiredLong;

@Slf4j
@Component("aiCvAnalysisDelegate")
@RequiredArgsConstructor
public class AiCvAnalysisDelegate implements JavaDelegate {

    private static final double MANUAL_REVIEW_SCORE = 0.0;

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

        AnalyzeCvResponse response;
        try {
            response = ragClient.analyzeCv(applicationId);
        } catch (WorkflowExternalServiceException exception) {
            if (exception.getStatus() == 422) {
                continueWithManualHrFiltering(execution, applicationId, exception);
                return;
            }
            throw exception;
        }

        if (response == null || response.score() == null) {
            throw new IllegalStateException("RAG service returned no score");
        }

        double score = Math.max(0.0, Math.min(100.0, response.score()));

        applicationClient.updateMatchingScore(
                applicationId,
                new UpdateMatchingScoreRequest(score)
        );

        execution.setVariable("aiScore", score);
        execution.setVariable("aiSummary", response.summary());
        execution.setVariable("aiRecommended", response.recommended());
        execution.setVariable("applicationStatus", ApplicationStatus.UNDER_AI_REVIEW.name());

        log.info("AI analysis finished: applicationId={}, score={}", applicationId, score);
    }

    private void continueWithManualHrFiltering(
            DelegateExecution execution,
            Long applicationId,
            WorkflowExternalServiceException exception
    ) {
        applicationClient.updateMatchingScore(
                applicationId,
                new UpdateMatchingScoreRequest(MANUAL_REVIEW_SCORE)
        );

        execution.setVariable("aiScore", MANUAL_REVIEW_SCORE);
        execution.setVariable(
                "aiSummary",
                "L’analyse IA n’a pas produit de résultat exploitable. Le CV doit être examiné manuellement par les ressources humaines."
        );
        execution.setVariable("aiRecommended", false);
        execution.setVariable("aiAnalysisUnavailable", true);
        execution.setVariable("aiAnalysisErrorStatus", exception.getStatus());
        execution.setVariable("applicationStatus", ApplicationStatus.UNDER_AI_REVIEW.name());

        log.warn(
                "RAG analysis returned 422; continuing with manual HR CV filtering: applicationId={}, reason={}",
                applicationId,
                exception.getMessage()
        );
    }
}
