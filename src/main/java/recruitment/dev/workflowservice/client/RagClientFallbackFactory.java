package recruitment.dev.workflowservice.client;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import recruitment.dev.workflowservice.dto.rag.AnalyzeCvResponse;

@Component
public class RagClientFallbackFactory implements FallbackFactory<RagClient> {
    @Override public RagClient create(Throwable cause) {
        return applicationId -> { throw WorkflowFeignFallbacks.unavailable("rag-service", cause); };
    }
}
