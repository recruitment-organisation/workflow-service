package recruitment.dev.workflowservice.service;

import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.runtime.ProcessInstanceQuery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import recruitment.dev.workflowservice.dto.StartRecruitmentRequest;
import recruitment.dev.workflowservice.dto.StartRecruitmentResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkflowServiceImplTest {

    @Mock private RuntimeService runtimeService;
    @Mock private ProcessInstanceQuery processInstanceQuery;
    @InjectMocks private WorkflowServiceImpl service;

    @Test
    void startsWorkflowWithBusinessDataAndInitialVariables() {
        StartRecruitmentRequest request = request();
        ProcessInstance instance = mock(ProcessInstance.class);
        when(runtimeService.createProcessInstanceQuery()).thenReturn(processInstanceQuery);
        when(processInstanceQuery.processInstanceBusinessKey("10")).thenReturn(processInstanceQuery);
        when(processInstanceQuery.active()).thenReturn(processInstanceQuery);
        when(processInstanceQuery.count()).thenReturn(0L);
        when(runtimeService.startProcessInstanceByKey(eq("recruitmentProcess"), eq("10"), anyMap())).thenReturn(instance);
        when(instance.getProcessInstanceId()).thenReturn("process-1");
        when(instance.getProcessDefinitionId()).thenReturn("recruitment:1:1");
        when(instance.getBusinessKey()).thenReturn("10");

        StartRecruitmentResponse response = service.startRecruitment(request);

        assertThat(response.getProcessInstanceId()).isEqualTo("process-1");
        assertThat(response.getBusinessKey()).isEqualTo("10");
        verify(runtimeService).startProcessInstanceByKey(eq("recruitmentProcess"), eq("10"), argThat(values ->
                values.get("applicationId").equals(10L) && values.get("applicationSubmitted").equals(true)));
    }

    @Test
    void refusesSecondActiveWorkflowForApplication() {
        when(runtimeService.createProcessInstanceQuery()).thenReturn(processInstanceQuery);
        when(processInstanceQuery.processInstanceBusinessKey("10")).thenReturn(processInstanceQuery);
        when(processInstanceQuery.active()).thenReturn(processInstanceQuery);
        when(processInstanceQuery.count()).thenReturn(1L);

        assertThatThrownBy(() -> service.startRecruitment(request()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("applicationId=10");
        verify(runtimeService, never()).startProcessInstanceByKey(anyString(), anyString(), anyMap());
    }

    private StartRecruitmentRequest request() {
        StartRecruitmentRequest request = new StartRecruitmentRequest();
        request.setApplicationId(10L); request.setCandidateId(20L); request.setJobId(30L); request.setCvId(40L);
        return request;
    }
}
