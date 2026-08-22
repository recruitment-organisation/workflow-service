package recruitment.dev.workflowservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import recruitment.dev.workflowservice.dto.joboffer.JobOfferResponse;
import recruitment.dev.workflowservice.security.FeignErrorConfig;
import recruitment.dev.workflowservice.security.FeignSecurityConfig;

@FeignClient(
        name = "job-offer-service",
        configuration = {FeignSecurityConfig.class, FeignErrorConfig.class},
        fallbackFactory = JobOfferClientFallbackFactory.class
)
public interface JobOfferClient {

    @GetMapping("/internal/job-offers/get/{jobOfferId}")
    JobOfferResponse getJobOfferById(@PathVariable("jobOfferId") Long jobOfferId);
}
