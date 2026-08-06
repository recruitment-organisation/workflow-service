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

import static recruitment.dev.workflowservice.util.WorkflowVariables.optionalString;
import static recruitment.dev.workflowservice.util.WorkflowVariables.requiredLong;

@Slf4j
@Component("cvRevisionNotificationDelegate")
@RequiredArgsConstructor
public class CvRevisionNotificationDelegate implements JavaDelegate {

    private final CandidateClient candidateClient;
    private final NotificationClient notificationClient;

    @Override
    public void execute(DelegateExecution execution) {
        Long candidateId = requiredLong(execution, "candidateId");
        Long applicationId = requiredLong(execution, "applicationId");
        CandidateResponse candidate = candidateClient.getCandidateById(candidateId);
        String missingSections = optionalString(execution, "missingCvSections");
        String details = missingSections == null || missingSections.isBlank()
                ? ""
                : " Sections à compléter : " + missingSections + ".";
        String message = "Bonjour " + candidate.firstName()
                + ", votre CV doit être corrigé et renvoyé dans les 2 heures."
                + details;

        execution.setVariable("cvRevisionNotificationSent", true);
        execution.setVariable("notificationType", NotificationType.CV_REVISION_REQUIRED.name());
        execution.setVariable("notificationSubject", "Révision de CV requise");
        execution.setVariable("notificationMessage", message);

        notificationClient.sendNotification(new SendNotificationRequest(
                candidateId,
                candidate.keycloakId(),
                applicationId,
                candidate.email(),
                NotificationType.CV_REVISION_REQUIRED,
                "Révision de CV requise",
                message
        ));

        log.info("CV revision notification sent: applicationId={}, candidateId={}", applicationId, candidateId);
    }
}
