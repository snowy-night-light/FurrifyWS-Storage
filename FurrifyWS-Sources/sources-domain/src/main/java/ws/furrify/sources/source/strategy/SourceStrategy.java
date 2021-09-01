package ws.furrify.sources.source.strategy;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.HashMap;

/**
 * Source strategy used to manage and update content using provided unique identifiers.
 *
 * @author sky
 */
public interface SourceStrategy {

    /**
     * Validate if given data is matching strategy requirements.
     *
     * @param data Data given in form of hash map containing information to access ex. artist, content.
     * @return Instance of Validation Result.
     */
    ValidationResult validate(HashMap<String, String> data);

    @Value
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    class ValidationResult {
        boolean valid;
        String reason;

        /**
         * @return Instance of ValidationResult with valid field set as true.
         */
        static ValidationResult valid() {
            return new ValidationResult(true, null);
        }

        /**
         * @return Instance of ValidationResult with valid field set as false and given reason.
         */
        static ValidationResult invalid(final String reason) {
            return new ValidationResult(false, reason);
        }
    }

}
