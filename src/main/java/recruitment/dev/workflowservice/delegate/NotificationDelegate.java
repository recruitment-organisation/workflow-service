package recruitment.dev.workflowservice.delegate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;
import recruitment.dev.workflowservice.client.CandidateClient;
import recruitment.dev.workflowservice.client.NotificationClient;
import recruitment.dev.workflowservice.dto.candidate.CandidateResponse;
import recruitment.dev.workflowservice.dto.notification.SendNotificationRequest;
import recruitment.dev.workflowservice.dto.notification.NotificationType;

import static recruitment.dev.workflowservice.util.WorkflowVariables.*;

@Slf4j
@Component("notificationDelegate")
@RequiredArgsConstructor
public class NotificationDelegate implements JavaDelegate {

    private final CandidateClient candidateClient;
    private final NotificationClient notificationClient;

    @Override
    public void execute(DelegateExecution execution) {
        Long candidateId = requiredLong(execution, "candidateId");
        Long applicationId = requiredLong(execution, "applicationId");
        CandidateResponse candidate = candidateClient.getCandidateById(candidateId);

        String activityName = execution.getCurrentActivityName();
        boolean welcome = activityName != null && activityName.toLowerCase().contains("welcome");

        NotificationType type = welcome ? NotificationType.WELCOME : NotificationType.REJECTION;
        String subject = welcome
                ? "Bienvenue dans notre entreprise"
                : "Mise à jour de votre candidature";

        String message;
        if (welcome) {
            message = "Félicitations " + candidate.firstName() + " " + candidate.lastName()
                    + ". Votre candidature a été acceptée et votre compte employé a été créé.";
        } else {
            String reason = optionalString(execution, "rejectionReason");
            message = "Bonjour " + candidate.firstName()
                    + ", votre candidature n'a pas été retenue. Motif: "
                    + (reason == null ? "non précisé" : reason) + ".";
        }

        execution.setVariable("notificationSent", true);
        execution.setVariable("notificationType", type.name());
        execution.setVariable("notificationSubject", subject);
        execution.setVariable("notificationMessage", message);

        notificationClient.sendNotification(new SendNotificationRequest(
                candidateId,
                candidate.keycloakId(),
                applicationId,
                candidate.email(),
                type,
                subject,
                message
        ));

        log.info(
                "Notification sent: applicationId={}, candidateId={}, type={}, email={}",
                applicationId,
                candidateId,
                type,
                candidate.email()
        );
    }
}
