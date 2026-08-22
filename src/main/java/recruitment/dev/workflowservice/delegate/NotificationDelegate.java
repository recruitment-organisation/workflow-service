package recruitment.dev.workflowservice.delegate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;
import recruitment.dev.workflowservice.client.CandidateClient;
import recruitment.dev.workflowservice.client.JobOfferClient;
import recruitment.dev.workflowservice.client.NotificationClient;
import recruitment.dev.workflowservice.dto.candidate.CandidateResponse;
import recruitment.dev.workflowservice.dto.joboffer.JobOfferResponse;
import recruitment.dev.workflowservice.dto.notification.SendNotificationRequest;
import recruitment.dev.workflowservice.dto.notification.NotificationType;

import static recruitment.dev.workflowservice.util.WorkflowVariables.*;

@Slf4j
@Component("notificationDelegate")
@RequiredArgsConstructor
public class NotificationDelegate implements JavaDelegate {

    private final CandidateClient candidateClient;
    private final JobOfferClient jobOfferClient;
    private final NotificationClient notificationClient;

    @Override
    public void execute(DelegateExecution execution) {
        Long candidateId = requiredLong(execution, "candidateId");
        Long applicationId = requiredLong(execution, "applicationId");
        CandidateResponse candidate = candidateClient.getCandidateById(candidateId);

        boolean accepted = booleanValue(execution, "managerApproved", false);

        NotificationType type = accepted ? NotificationType.OFFER_ACCEPTED : NotificationType.REJECTION;
        JobOfferResponse offer = accepted ? loadJobOffer(execution) : null;
        String position = offer == null || offer.title() == null || offer.title().isBlank()
                ? "le poste correspondant à votre candidature"
                : "le poste de " + offer.title();
        String subject = accepted
                ? "Votre offre d'emploi" + (offer == null || offer.title() == null ? "" : " – " + offer.title())
                : "Mise à jour de votre candidature";

        String message;
        if (accepted) {
            String offerDetails = offerDetails(offer);
            message = "Bonjour " + candidate.firstName() + " " + candidate.lastName() + ",\n\n"
                    + "Félicitations. À la suite de vos entretiens, nous avons le plaisir de vous confirmer "
                    + "que votre candidature pour " + position + " a été retenue."
                    + offerDetails + "\n\n"
                    + "Notre équipe RH vous contactera pour finaliser les conditions et les documents d'embauche.\n\n"
                    + "Bienvenue dans l'équipe.";
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

    private JobOfferResponse loadJobOffer(DelegateExecution execution) {
        Long jobOfferId = requiredLong(execution, "jobId");
        try {
            return jobOfferClient.getJobOfferById(jobOfferId);
        } catch (RuntimeException exception) {
            log.warn("Unable to load job offer details for acceptance email: jobOfferId={}", jobOfferId, exception);
            return null;
        }
    }

    private String offerDetails(JobOfferResponse offer) {
        if (offer == null) {
            return "";
        }
        StringBuilder details = new StringBuilder();
        if (offer.employmentType() != null && !offer.employmentType().isBlank()) {
            details.append("\nType de contrat : ").append(offer.employmentType()).append('.');
        }
        if (offer.location() != null && !offer.location().isBlank()) {
            details.append("\nLocalisation : ").append(offer.location()).append('.');
        }
        return details.toString();
    }
}
