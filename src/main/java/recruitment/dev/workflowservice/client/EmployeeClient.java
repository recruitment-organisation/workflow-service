package recruitment.dev.workflowservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import recruitment.dev.workflowservice.dto.employee.CreateEmployeeRequest;
import recruitment.dev.workflowservice.dto.employee.EmployeeResponse;
import recruitment.dev.workflowservice.security.FeignErrorConfig;
import recruitment.dev.workflowservice.security.FeignSecurityConfig;

@FeignClient(
        name = "employee-service",
        configuration = {FeignSecurityConfig.class, FeignErrorConfig.class},
        fallbackFactory = EmployeeClientFallbackFactory.class
)
public interface EmployeeClient {

    @PostMapping("/employee/create")
    EmployeeResponse createEmployee(@RequestBody CreateEmployeeRequest request);
}
