package recruitment.dev.workflowservice.dto.rag;

public record AnalyzeCvResponse(
        Double score,
        String decision,
        String summary
) {
    public boolean recommended() {
        return "RECOMMENDED".equals(decision);
    }
}
