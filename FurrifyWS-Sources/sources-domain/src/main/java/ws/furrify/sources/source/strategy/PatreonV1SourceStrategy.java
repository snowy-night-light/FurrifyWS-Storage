package ws.furrify.sources.source.strategy;

import java.util.HashMap;

/**
 * Version 1 of Patreon Art Strategy.
 * Represents Patreon.com website for creator posts.
 *
 * @author sky
 */
public class PatreonV1SourceStrategy implements SourceStrategy {

    @Override
    public ValidationResult validate(final HashMap<String, String> data) {
        // TODO Implement me

        return ValidationResult.valid();
    }
}
