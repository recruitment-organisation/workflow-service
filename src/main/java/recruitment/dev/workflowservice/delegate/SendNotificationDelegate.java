package recruitment.dev.workflowservice.delegate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;
import recruitment.dev.workflowservice.client.CandidateClient;
import recruitment.dev.workflowservice.client.NotificationClient;
import recruitment.dev.workflowservice.dto.candidate.CandidateResponse;
import recruitment.dev.workflowservice.dto.notification.NotificationType;
import recruitment.dev.workflowservice.dto.notification.SendNotificationRequest;

import static recruitment.dev.workflowservice.util.WorkflowVariables.requiredLong;

@Slf4j
@Component("sendNotificationDelegate")
@RequiredArgsConstructor
public class SendNotificationDelegate implements JavaDelegate {

    private final CandidateClient candidateClient;
    private final NotificationClient notificationClient;

    @Override
    public void execute(DelegateExecution execution) {
        Long candidateId = requiredLong(execution, "candidateId");
        Long applicationId = requiredLong(execution, "applicationId");
        CandidateResponse candidate = candidateClient.getCandidateById(candidateId);

        String message = "Bonjour " + candidate.firstName()
                + ", le délai de correction du CV est expiré. Votre candidature a été clôturée.";

        execution.setVariable("timeoutNotificationSent", true);
        execution.setVariable("notificationType", NotificationType.CV_TIMEOUT.name());
        execution.setVariable("notificationSubject", "Délai de correction du CV expiré");
        execution.setVariable("notificationMessage", message);

        notificationClient.sendNotification(new SendNotificationRequest(
                candidateId,
                candidate.keycloakId(),
                applicationId,
                candidate.email(),
                NotificationType.CV_TIMEOUT,
                "Délai de correction du CV expiré",
                message
        ));

        log.info(
                "Timeout notification sent: applicationId={}, candidateId={}, email={}",
                applicationId,
                candidateId,
                candidate.email()
        );
    }
}
