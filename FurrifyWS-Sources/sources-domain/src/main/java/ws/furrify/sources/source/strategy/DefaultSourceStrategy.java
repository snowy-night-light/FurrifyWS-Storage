package ws.furrify.sources.source.strategy;

import java.util.HashMap;

/**
 * Default strategy if the current one was not found.
 *
 * @author sky
 */
public class DefaultSourceStrategy implements SourceStrategy {
    @Override
    public ValidationResult validate(final HashMap<String, String> data) {
        if (data == null) {
            return ValidationResult.invalid("Data cannot be null.");
        }

        return ValidationResult.valid();
    }
}
