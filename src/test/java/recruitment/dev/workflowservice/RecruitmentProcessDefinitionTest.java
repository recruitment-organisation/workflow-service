package recruitment.dev.workflowservice;

import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecruitmentProcessDefinitionTest {

    @Test
    void recruitmentProcessIsSchemaValid() throws Exception {
        BpmnXMLConverter converter = new BpmnXMLConverter();
        try (InputStream process = getClass().getResourceAsStream("/processes/Recruitment_Process.bpmn20.xml")) {
            assertNotNull(process);
            converter.validateModel(() -> process);
        }
    }

    @Test
    void approvedHrInterviewContinuesToTechnicalInterview() throws Exception {
        try (InputStream process = getClass().getResourceAsStream("/processes/Recruitment_Process.bpmn20.xml")) {
            assertNotNull(process);
            String xml = new String(process.readAllBytes(), StandardCharsets.UTF_8);
            assertTrue(xml.contains("sourceRef=\"sid-53587564-8F81-4247-B32D-BB1F2EE4CF86\" targetRef=\"technicalInterview\""));
        }
    }
}
