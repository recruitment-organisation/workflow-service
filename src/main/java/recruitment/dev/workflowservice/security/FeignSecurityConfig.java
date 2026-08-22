package recruitment.dev.workflowservice.security;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@Configuration
public class FeignSecurityConfig {

    @Bean
    public RequestInterceptor workflowTokenInterceptor() {
        return requestTemplate -> {
            if (requestTemplate.headers().containsKey("Authorization")) {
                return;
            }

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication instanceof JwtAuthenticationToken jwtAuthentication) {
                requestTemplate.header(
                        "Authorization",
                        "Bearer " + jwtAuthentication.getToken().getTokenValue()
                );
            }
        };
    }
}
