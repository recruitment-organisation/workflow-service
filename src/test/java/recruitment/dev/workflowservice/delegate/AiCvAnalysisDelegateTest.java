package recruitment.dev.workflowservice.delegate;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import recruitment.dev.workflowservice.TestDelegateExecutionFactory;
import recruitment.dev.workflowservice.client.ApplicationClient;
import recruitment.dev.workflowservice.client.RagClient;
import recruitment.dev.workflowservice.dto.CvTemplateValidationResult;
import recruitment.dev.workflowservice.dto.application.ApplicationResponse;
import recruitment.dev.workflowservice.dto.application.ApplicationStatus;
import recruitment.dev.workflowservice.dto.application.UpdateMatchingScoreRequest;
import recruitment.dev.workflowservice.dto.rag.AnalyzeCvResponse;

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
    }

    private static final class RecordingApplicationClient implements ApplicationClient {

        private String lastStatus;

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
            return new ApplicationResponse(applicationId, 4L, 9L, 7L, ApplicationStatus.UNDER_AI_REVIEW, request.matchingScore());
        }
    }
}
