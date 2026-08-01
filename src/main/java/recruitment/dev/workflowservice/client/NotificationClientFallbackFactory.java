package recruitment.dev.workflowservice.client;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import recruitment.dev.workflowservice.dto.notification.SendNotificationRequest;

@Component
public class NotificationClientFallbackFactory implements FallbackFactory<NotificationClient> {
    @Override public NotificationClient create(Throwable cause) {
        return request -> { throw WorkflowFeignFallbacks.unavailable("notification-service", cause); };
    }
}
