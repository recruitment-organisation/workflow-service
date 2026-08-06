package recruitment.dev.workflowservice.delegate;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import recruitment.dev.workflowservice.TestDelegateExecutionFactory;
import recruitment.dev.workflowservice.client.ApplicationClient;
import recruitment.dev.workflowservice.client.RagClient;
import recruitment.dev.workflowservice.dto.CvTemplateValidationResult;
import recruitment.dev.workflowservice.dto.application.ApplicationResponse;
import recruitment.dev.workflowservice.dto.application.ApplicationStatus;
import recruitment.dev.workflowservice.dto.application.UpdateApplicationWorkflowStateRequest;
import recruitment.dev.workflowservice.dto.application.UpdateMatchingScoreRequest;
import recruitment.dev.workflowservice.dto.rag.AnalyzeCvResponse;
import recruitment.dev.workflowservice.exception.WorkflowExternalServiceException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AiCvAnalysisDelegateTest {

    @Test
    void shouldUpdateWorkflowVariablesAndPersistScore() {
        RecordingApplicationClient applicationClient = new RecordingApplicationClient();
        RagClient ragClient = applicationId -> new AnalyzeCvResponse(84.6, "RECOMMENDED", "Compatible avec le poste");

        AiCvAnalysisDelegate delegate = new AiCvAnalysisDelegate(ragClient, applicationClient);
        DelegateExecution execution = TestDelegateExecutionFactory.create(
                Map.of(
                        "applicationId", 10L,
                        "candidateId", 4L,
                        "jobId", 9L,
                        "cvId", 7L
                )
        );

        delegate.execute(execution);

        assertEquals("UNDER_AI_REVIEW", execution.getVariable("applicationStatus"));
        assertEquals(84.6, execution.getVariable("aiScore"));
        assertEquals("Compatible avec le poste", execution.getVariable("aiSummary"));
        assertEquals(true, execution.getVariable("aiRecommended"));
        assertEquals("UNDER_AI_REVIEW", applicationClient.lastStatus);
        assertEquals(84.6, applicationClient.lastMatchingScore);
    }

    @Test
    void shouldContinueToManualHrFilteringWhenRagCannotAnalyzeTheCv() {
        RecordingApplicationClient applicationClient = new RecordingApplicationClient();
        RagClient ragClient = applicationId -> {
            throw new WorkflowExternalServiceException("RagClient#analyzeCv(Long)", 422, "PDF_TEXT_EMPTY");
        };

        AiCvAnalysisDelegate delegate = new AiCvAnalysisDelegate(ragClient, applicationClient);
        DelegateExecution execution = TestDelegateExecutionFactory.create(
                Map.of(
                        "applicationId", 10L,
                        "candidateId", 4L,
                        "jobId", 9L,
                        "cvId", 7L
                )
        );

        delegate.execute(execution);

        assertEquals(0.0, execution.getVariable("aiScore"));
        assertEquals(false, execution.getVariable("aiRecommended"));
        assertEquals(true, execution.getVariable("aiAnalysisUnavailable"));
        assertEquals(422, execution.getVariable("aiAnalysisErrorStatus"));
        assertEquals(0.0, applicationClient.lastMatchingScore);
    }

    private static final class RecordingApplicationClient implements ApplicationClient {

        private String lastStatus;
        private Double lastMatchingScore;

        @Override
        public CvTemplateValidationResult validateCvTemplate(Long applicationId) {
            return null;
        }

        @Override
        public ApplicationResponse updateStatus(Long applicationId, String status) {
            this.lastStatus = status;
            return new ApplicationResponse(applicationId, 4L, 9L, 7L, ApplicationStatus.valueOf(status), null);
        }

        @Override
        public ApplicationResponse updateMatchingScore(Long applicationId, UpdateMatchingScoreRequest request) {
            this.lastMatchingScore = request.matchingScore();
            return new ApplicationResponse(applicationId, 4L, 9L, 7L, ApplicationStatus.UNDER_AI_REVIEW, request.matchingScore());
        }

        @Override
        public void updateWorkflowState(Long applicationId, UpdateApplicationWorkflowStateRequest request) {
            // Not relevant for this delegate.
        }
    }
}
