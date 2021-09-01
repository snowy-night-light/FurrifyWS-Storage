package ws.furrify.sources.source;

import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.exception.InvalidDataGivenException;
import ws.furrify.sources.source.dto.SourceDTO;

import java.time.ZonedDateTime;
import java.util.UUID;

final class SourceFactory {

    Source from(SourceDTO sourceDTO) {
        // Validation result
        var validationResult = sourceDTO.getSourceStrategy().validate(sourceDTO.getData());

        // Was validation successful
        if (!validationResult.isValid()) {
            throw new InvalidDataGivenException(Errors.VALIDATION_FAILED.getErrorMessage(
                    sourceDTO.getSourceStrategy().getClass().getSimpleName(),
                    validationResult.getReason()
            ));
        }

        SourceSnapshot sourceSnapshot = SourceSnapshot.builder()
                .id(sourceDTO.getId())
                .sourceId(
                        sourceDTO.getSourceId() != null ? sourceDTO.getSourceId() : UUID.randomUUID()
                )
                .ownerId(sourceDTO.getOwnerId())
                .sourceStrategy(sourceDTO.getSourceStrategy())
                .data(sourceDTO.getData())
                .createDate(
                        sourceDTO.getCreateDate() != null ? sourceDTO.getCreateDate() : ZonedDateTime.now()
                )
                .build();

        return Source.restore(sourceSnapshot);
    }

}
