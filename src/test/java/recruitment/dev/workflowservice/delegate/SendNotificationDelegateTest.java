package recruitment.dev.workflowservice.delegate;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import recruitment.dev.workflowservice.TestDelegateExecutionFactory;
import recruitment.dev.workflowservice.client.CandidateClient;
import recruitment.dev.workflowservice.client.NotificationClient;
import recruitment.dev.workflowservice.dto.candidate.CandidateResponse;
import recruitment.dev.workflowservice.dto.notification.SendNotificationRequest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SendNotificationDelegateTest {

    @Test
    void shouldSendCvTimeoutNotification() {
        CandidateClient candidateClient = candidateId -> new CandidateResponse(
                candidateId,
                "kc-2",
                "Sara",
                "Ben Ali",
                "sara@example.com",
                "+21611111111",
                "Sousse"
        );
        RecordingNotificationClient notificationClient = new RecordingNotificationClient();

        SendNotificationDelegate delegate = new SendNotificationDelegate(candidateClient, notificationClient);
        DelegateExecution execution = TestDelegateExecutionFactory.create(
                Map.of(
                        "candidateId", 8L,
                        "applicationId", 14L
                )
        );

        delegate.execute(execution);

        assertEquals(true, execution.getVariable("timeoutNotificationSent"));
        assertEquals("CV_TIMEOUT", execution.getVariable("notificationType"));
        assertEquals("Délai de correction du CV expiré", execution.getVariable("notificationSubject"));
        assertNotNull(notificationClient.lastRequest);
        assertEquals(8L, notificationClient.lastRequest.candidateId());
        assertEquals("kc-2", notificationClient.lastRequest.candidateKeycloakId());
        assertEquals(14L, notificationClient.lastRequest.applicationId());
        assertEquals("sara@example.com", notificationClient.lastRequest.recipientEmail());
        assertEquals("CV_TIMEOUT", notificationClient.lastRequest.type().name());
    }

    private static final class RecordingNotificationClient implements NotificationClient {

        private SendNotificationRequest lastRequest;

        @Override
        public void sendNotification(SendNotificationRequest request) {
            this.lastRequest = request;
        }
    }
}
