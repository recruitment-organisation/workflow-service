package recruitment.dev.workflowservice.security;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import recruitment.dev.workflowservice.exception.WorkflowExternalServiceException;

@Configuration
public class FeignErrorConfig {

    @Bean
    public ErrorDecoder workflowErrorDecoder() {
        return (methodKey, response) -> new WorkflowExternalServiceException(
                methodKey,
                response.status(),
                extractReason(response)
        );
    }

    private String extractReason(Response response) {
        return response.reason() == null ? "External service call failed" : response.reason();
    }
}
