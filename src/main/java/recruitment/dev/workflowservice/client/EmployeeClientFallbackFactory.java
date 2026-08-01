package recruitment.dev.workflowservice.client;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import recruitment.dev.workflowservice.dto.employee.CreateEmployeeRequest;
import recruitment.dev.workflowservice.dto.employee.EmployeeResponse;

@Component
public class EmployeeClientFallbackFactory implements FallbackFactory<EmployeeClient> {
    @Override public EmployeeClient create(Throwable cause) {
        return request -> { throw WorkflowFeignFallbacks.unavailable("employee-service", cause); };
    }
}
