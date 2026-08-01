package recruitment.dev.workflowservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;

import recruitment.dev.workflowservice.dto.rag.AnalyzeCvResponse;
import recruitment.dev.workflowservice.security.FeignErrorConfig;
import recruitment.dev.workflowservice.security.FeignSecurityConfig;

@FeignClient(
        name = "rag-service",
        configuration = {FeignSecurityConfig.class, FeignErrorConfig.class},
        fallbackFactory = RagClientFallbackFactory.class
)
public interface RagClient {

    @PostMapping("/rag/applications/{applicationId}/analyze")
    AnalyzeCvResponse analyzeCv(@PathVariable("applicationId") Long applicationId);
}
