package recruitment.dev.workflowservice.util;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import recruitment.dev.workflowservice.TestDelegateExecutionFactory;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WorkflowVariablesTest {

    @Test
    void shouldReadRequiredAndOptionalVariables() {
        DelegateExecution execution = TestDelegateExecutionFactory.create(
                Map.of(
                        "applicationId", "12",
                        "departmentId", 7L,
                        "position", " Java Developer ",
                        "aiScore", "82.5",
                        "hrApproved", "true"
                )
        );

        assertEquals(12L, WorkflowVariables.requiredLong(execution, "applicationId"));
        assertEquals(7L, WorkflowVariables.optionalLong(execution, "departmentId"));
        assertEquals("Java Developer", WorkflowVariables.requiredString(execution, "position"));
        assertEquals(82.5, WorkflowVariables.optionalDouble(execution, "aiScore"));
        assertTrue(WorkflowVariables.booleanValue(execution, "hrApproved", false));
        assertFalse(WorkflowVariables.booleanValue(execution, "managerApproved", false));
    }

    @Test
    void shouldFailWhenRequiredVariableIsMissing() {
        DelegateExecution execution = TestDelegateExecutionFactory.create(Map.of());

        assertThrows(
                IllegalArgumentException.class,
                () -> WorkflowVariables.requiredLong(execution, "applicationId")
        );
    }
}
