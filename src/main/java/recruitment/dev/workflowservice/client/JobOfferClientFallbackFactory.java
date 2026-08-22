package recruitment.dev.workflowservice.client;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class JobOfferClientFallbackFactory implements FallbackFactory<JobOfferClient> {

    @Override
    public JobOfferClient create(Throwable cause) {
        return jobOfferId -> {
            throw WorkflowFeignFallbacks.unavailable("job-offer-service", cause);
        };
    }
}
