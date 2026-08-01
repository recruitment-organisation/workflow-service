package recruitment.dev.workflowservice.delegate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;
import recruitment.dev.workflowservice.client.ApplicationClient;
import recruitment.dev.workflowservice.dto.CvTemplateValidationResult;

@Slf4j
@Component("cvTemplateValidationDelegate")
@RequiredArgsConstructor
public class CvTemplateValidationDelegate implements JavaDelegate {

    private final ApplicationClient applicationClient;

    @Override
    public void execute(DelegateExecution execution) {

        Long applicationId = Long.valueOf(
                execution.getVariable("applicationId").toString()
        );

        log.info("Starting CV template validation for application {}", applicationId);

        CvTemplateValidationResult result =
                applicationClient.validateCvTemplate(applicationId);

        execution.setVariable("cvValid", result.isValid());
        execution.setVariable("missingCvSections", result.getMissingSections());

        log.info(
                "CV validation finished : valid={}, missingSections={}",
                result.isValid(),
                result.getMissingSections()
        );
    }
}