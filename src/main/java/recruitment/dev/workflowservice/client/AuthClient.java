package recruitment.dev.workflowservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import recruitment.dev.workflowservice.dto.auth.UpdateUserRoleRequest;
import recruitment.dev.workflowservice.security.FeignErrorConfig;
import recruitment.dev.workflowservice.security.FeignSecurityConfig;

@FeignClient(
        name = "auth-service",
        configuration = {FeignSecurityConfig.class, FeignErrorConfig.class},
        fallbackFactory = AuthClientFallbackFactory.class
)
public interface AuthClient {

    @PatchMapping("/auth/users/{keycloakId}/role")
    void updateRole(
            @PathVariable("keycloakId") String keycloakId,
            @RequestBody UpdateUserRoleRequest request
    );
}
