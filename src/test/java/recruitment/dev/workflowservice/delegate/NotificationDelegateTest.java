package recruitment.dev.workflowservice.delegate;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import recruitment.dev.workflowservice.TestDelegateExecutionFactory;
import recruitment.dev.workflowservice.client.CandidateClient;
import recruitment.dev.workflowservice.client.JobOfferClient;
import recruitment.dev.workflowservice.client.NotificationClient;
import recruitment.dev.workflowservice.dto.candidate.CandidateResponse;
import recruitment.dev.workflowservice.dto.joboffer.JobOfferResponse;
import recruitment.dev.workflowservice.dto.notification.SendNotificationRequest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NotificationDelegateTest {

    @Test
    void shouldSendRejectionNotification() {
        CandidateClient candidateClient = candidateId -> new CandidateResponse(
                candidateId,
                "kc-1",
                "Jean",
                "Dupont",
                "jean@example.com",
                "+21612345678",
                "Tunis"
        );
        RecordingNotificationClient notificationClient = new RecordingNotificationClient();

        JobOfferClient jobOfferClient = jobOfferId -> null;
        NotificationDelegate delegate = new NotificationDelegate(candidateClient, jobOfferClient, notificationClient);
        DelegateExecution execution = TestDelegateExecutionFactory.create(
                Map.of(
                        "candidateId", 5L,
                        "applicationId", 11L,
                        "rejectionReason", "AI_SCORE_BELOW_70"
                ),
                "Send rejection notification"
        );

        delegate.execute(execution);

        assertEquals(true, execution.getVariable("notificationSent"));
        assertEquals("REJECTION", execution.getVariable("notificationType"));
        assertEquals("Mise à jour de votre candidature", execution.getVariable("notificationSubject"));
        assertTrue(String.valueOf(execution.getVariable("notificationMessage")).contains("AI_SCORE_BELOW_70"));
        assertNotNull(notificationClient.lastRequest);
        assertEquals(5L, notificationClient.lastRequest.candidateId());
        assertEquals("kc-1", notificationClient.lastRequest.candidateKeycloakId());
        assertEquals(11L, notificationClient.lastRequest.applicationId());
        assertEquals("jean@example.com", notificationClient.lastRequest.recipientEmail());
        assertEquals("REJECTION", notificationClient.lastRequest.type().name());
    }

    @Test
    void shouldSendAnEmploymentOfferAfterManagerAcceptance() {
        CandidateClient candidateClient = candidateId -> new CandidateResponse(
                candidateId,
                "kc-2",
                "Amina",
                "Ben Salah",
                "amina@example.com",
                "+21698765432",
                "Sousse"
        );
        JobOfferClient jobOfferClient = jobOfferId -> new JobOfferResponse(
                jobOfferId,
                "Ingénieure DevOps",
                "Tunis",
                "CDI"
        );
        RecordingNotificationClient notificationClient = new RecordingNotificationClient();
        NotificationDelegate delegate = new NotificationDelegate(candidateClient, jobOfferClient, notificationClient);
        DelegateExecution execution = TestDelegateExecutionFactory.create(
                Map.of(
                        "candidateId", 6L,
                        "applicationId", 12L,
                        "jobId", 9L,
                        "managerApproved", true
                ),
                "Send employment offer notification"
        );

        delegate.execute(execution);

        assertEquals("OFFER_ACCEPTED", execution.getVariable("notificationType"));
        assertTrue(String.valueOf(execution.getVariable("notificationSubject")).contains("Ingénieure DevOps"));
        assertTrue(String.valueOf(execution.getVariable("notificationMessage")).contains("CDI"));
        assertTrue(String.valueOf(execution.getVariable("notificationMessage")).contains("Tunis"));
        assertNotNull(notificationClient.lastRequest);
        assertEquals("OFFER_ACCEPTED", notificationClient.lastRequest.type().name());
        assertEquals("amina@example.com", notificationClient.lastRequest.recipientEmail());
    }

    private static final class RecordingNotificationClient implements NotificationClient {

        private SendNotificationRequest lastRequest;

        @Override
        public void sendNotification(SendNotificationRequest request) {
            this.lastRequest = request;
        }
    }
}
