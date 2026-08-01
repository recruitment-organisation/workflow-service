package recruitment.dev.workflowservice.client;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import recruitment.dev.workflowservice.dto.candidate.CandidateResponse;

@Component
public class CandidateClientFallbackFactory implements FallbackFactory<CandidateClient> {
    @Override public CandidateClient create(Throwable cause) {
        return candidateId -> { throw WorkflowFeignFallbacks.unavailable("candidate-service", cause); };
    }
}
