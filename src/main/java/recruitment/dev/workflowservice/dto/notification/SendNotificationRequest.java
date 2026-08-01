package recruitment.dev.workflowservice.dto.notification;

public record SendNotificationRequest(
        Long candidateId,
        String candidateKeycloakId,
        Long applicationId,
        String recipientEmail,
        NotificationType type,
        String subject,
        String message
) {
}
