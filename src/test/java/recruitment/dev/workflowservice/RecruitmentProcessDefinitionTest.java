package recruitment.dev.workflowservice;

import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class RecruitmentProcessDefinitionTest {

    @Test
    void recruitmentProcessIsSchemaValid() throws Exception {
        BpmnXMLConverter converter = new BpmnXMLConverter();
        try (InputStream process = getClass().getResourceAsStream("/processes/Recruitment_Process.bpmn20.xml")) {
            assertNotNull(process);
            converter.validateModel(() -> process);
        }
    }
}
