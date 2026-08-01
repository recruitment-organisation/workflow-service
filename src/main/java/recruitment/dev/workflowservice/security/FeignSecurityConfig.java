package recruitment.dev.workflowservice.security;

import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
@RequiredArgsConstructor
@Configuration
public class FeignSecurityConfig {


    private final OAuth2AuthorizedClientManager authorizedClientManager;

    @Bean
    public RequestInterceptor workflowTokenInterceptor() {
        return requestTemplate -> {

            OAuth2AuthorizeRequest request =
                    OAuth2AuthorizeRequest
                            .withClientRegistrationId("workflow-client")
                            .principal("workflow-service")
                            .build();

            var authorizedClient =
                    authorizedClientManager.authorize(request);

            if (authorizedClient == null) {
                throw new IllegalStateException(
                        "Unable to obtain workflow-service token"
                );
            }

            String token = authorizedClient
                    .getAccessToken()
                    .getTokenValue();

            requestTemplate.header(
                    "Authorization",
                    "Bearer " + token
            );
        };
    }
}