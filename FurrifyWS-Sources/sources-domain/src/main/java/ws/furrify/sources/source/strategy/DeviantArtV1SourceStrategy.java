package ws.furrify.sources.source.strategy;

import java.util.HashMap;

/**
 * Version 1 of Deviant Art Strategy.
 * Represents DeviantArt.com website for artists and content.
 *
 * @author sky
 */
public class DeviantArtV1SourceStrategy implements SourceStrategy {

    @Override
    public ValidationResult validate(final HashMap<String, String> data) {
        // TODO Implement me

        return ValidationResult.valid();
    }
}
