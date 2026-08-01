package recruitment.dev.workflowservice.client;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import recruitment.dev.workflowservice.dto.auth.UpdateUserRoleRequest;

@Component
public class AuthClientFallbackFactory implements FallbackFactory<AuthClient> {
    @Override public AuthClient create(Throwable cause) {
        return (keycloakId, request) -> { throw WorkflowFeignFallbacks.unavailable("auth-service", cause); };
    }
}
