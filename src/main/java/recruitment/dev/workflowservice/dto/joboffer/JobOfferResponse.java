package recruitment.dev.workflowservice.dto.joboffer;

public record JobOfferResponse(
        Long id,
        String title,
        String location,
        String employmentType
) {
}
