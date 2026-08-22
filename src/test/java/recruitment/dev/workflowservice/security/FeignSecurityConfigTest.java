package recruitment.dev.workflowservice.security;

import feign.RequestTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class FeignSecurityConfigTest {

    private final FeignSecurityConfig config = new FeignSecurityConfig();

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void relaysTheValidatedJwt() {
        Jwt jwt = Jwt.withTokenValue("validated-token")
                .header("alg", "none")
                .subject("user-1")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(300))
                .build();
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt));
        RequestTemplate template = new RequestTemplate();

        config.workflowTokenInterceptor().apply(template);

        assertThat(template.headers().get("Authorization"))
                .containsExactly("Bearer validated-token");
    }

    @Test
    void doesNotReplaceAnExplicitAuthorizationHeader() {
        RequestTemplate template = new RequestTemplate();
        template.header("Authorization", "Bearer explicit-token");

        config.workflowTokenInterceptor().apply(template);

        assertThat(template.headers().get("Authorization"))
                .containsExactly("Bearer explicit-token");
    }

    @Test
    void doesNotAddAHeaderWithoutAnAuthenticatedJwt() {
        RequestTemplate template = new RequestTemplate();

        config.workflowTokenInterceptor().apply(template);

        assertThat(template.headers()).doesNotContainKey("Authorization");
    }
}
