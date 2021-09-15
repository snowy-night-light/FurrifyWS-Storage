package ws.furrify.sources.source;

import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.exception.InvalidDataGivenException;
import ws.furrify.sources.source.dto.SourceDTO;

import java.time.ZonedDateTime;
import java.util.UUID;

final class SourceFactory {

    Source from(SourceDTO sourceDTO) {
        // Validation result
        var validationResult = sourceDTO.getStrategy().validate(sourceDTO.getData());

        // Was validation successful
        if (!validationResult.isValid()) {
            throw new InvalidDataGivenException(Errors.VALIDATION_FAILED.getErrorMessage(
                    sourceDTO.getStrategy().getClass().getSimpleName(),
                    validationResult.getReason()
            ));
        }

        SourceSnapshot sourceSnapshot = SourceSnapshot.builder()
                .id(sourceDTO.getId())
                .postId(sourceDTO.getPostId())
                .originId(sourceDTO.getOriginId())
                .sourceId(
                        sourceDTO.getSourceId() != null ? sourceDTO.getSourceId() : UUID.randomUUID()
                )
                .ownerId(sourceDTO.getOwnerId())
                .strategy(sourceDTO.getStrategy())
                .data(sourceDTO.getData())
                .originType(sourceDTO.getOriginType())
                .createDate(
                        sourceDTO.getCreateDate() != null ? sourceDTO.getCreateDate() : ZonedDateTime.now()
                )
                .build();

        return Source.restore(sourceSnapshot);
    }

}
