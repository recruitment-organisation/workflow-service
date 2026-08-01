package recruitment.dev.workflowservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import recruitment.dev.workflowservice.dto.notification.SendNotificationRequest;
import recruitment.dev.workflowservice.security.FeignErrorConfig;
import recruitment.dev.workflowservice.security.FeignSecurityConfig;

@FeignClient(
        name = "notification-service",
        configuration = {FeignSecurityConfig.class, FeignErrorConfig.class},
        fallbackFactory = NotificationClientFallbackFactory.class
)
public interface NotificationClient {

    @PostMapping("/api/notifications/send")
    void sendNotification(@RequestBody SendNotificationRequest request);
}
