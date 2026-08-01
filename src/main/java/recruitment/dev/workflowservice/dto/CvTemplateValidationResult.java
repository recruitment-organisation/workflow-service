package recruitment.dev.workflowservice.dto;

import lombok.Data;

import java.util.List;

@Data
public class CvTemplateValidationResult {

    private boolean valid;
    private List<String> missingSections;

}