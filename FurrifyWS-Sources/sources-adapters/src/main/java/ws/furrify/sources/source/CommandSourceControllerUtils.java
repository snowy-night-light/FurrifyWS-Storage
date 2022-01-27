package ws.furrify.sources.source;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.exception.HardLimitForEntityTypeException;

import java.util.UUID;

@Component
@RequiredArgsConstructor
class CommandSourceControllerUtils {

    private final SourceRepository sourceRepository;
    @Value("${furrify.limits.sources}")
    private long sourcesLimitPerUser;

    /**
     * Check if number of sources has exceeded the maximum per user. If so throw exception.
     *
     * @param userId User uuid.
     * @throws HardLimitForEntityTypeException Exception with information about the limit number and entity it has been exceeded.
     */
    public void checkForSourceHardLimit(@NonNull final UUID userId) throws HardLimitForEntityTypeException {
        // Hard limit for sources
        long userSourcesCount = sourceRepository.countSourcesByUserId(userId);
        if (userSourcesCount >= sourcesLimitPerUser) {
            throw new HardLimitForEntityTypeException(
                    Errors.HARD_LIMIT_FOR_ENTITY_TYPE.getErrorMessage(sourcesLimitPerUser, "Source")
            );
        }
    }

}
