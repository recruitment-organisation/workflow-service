package recruitment.dev.workflowservice.delegate;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import recruitment.dev.workflowservice.TestDelegateExecutionFactory;
import recruitment.dev.workflowservice.client.CandidateClient;
import recruitment.dev.workflowservice.client.NotificationClient;
import recruitment.dev.workflowservice.dto.candidate.CandidateResponse;
import recruitment.dev.workflowservice.dto.notification.SendNotificationRequest;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CvRevisionNotificationDelegateTest {

    @Test
    void sendsRevisionNotificationToTheCandidate() {
        CandidateClient candidateClient = candidateId -> new CandidateResponse(
                candidateId,
                "candidate-keycloak-id",
                "Sara",
                "Ben Ali",
                "sara@example.com",
                "+21611111111",
                "Sousse"
        );
        RecordingNotificationClient notificationClient = new RecordingNotificationClient();
        DelegateExecution execution = TestDelegateExecutionFactory.create(Map.of(
                "candidateId", 8L,
                "applicationId", 14L,
                "missingCvSections", List.of("SKILLS", "EXPERIENCE")
        ));

        new CvRevisionNotificationDelegate(candidateClient, notificationClient).execute(execution);

        assertEquals(true, execution.getVariable("cvRevisionNotificationSent"));
        assertEquals("CV_REVISION_REQUIRED", execution.getVariable("notificationType"));
        assertNotNull(notificationClient.lastRequest);
        assertEquals("candidate-keycloak-id", notificationClient.lastRequest.candidateKeycloakId());
        assertEquals("CV_REVISION_REQUIRED", notificationClient.lastRequest.type().name());
        assertEquals("Révision de CV requise", notificationClient.lastRequest.subject());
    }

    private static final class RecordingNotificationClient implements NotificationClient {
        private SendNotificationRequest lastRequest;

        @Override
        public void sendNotification(SendNotificationRequest request) {
            this.lastRequest = request;
        }
    }
}
