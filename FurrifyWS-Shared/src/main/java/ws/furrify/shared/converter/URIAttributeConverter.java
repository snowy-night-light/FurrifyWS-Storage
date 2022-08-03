package ws.furrify.shared.converter;

import javax.persistence.AttributeConverter;
import java.net.URI;

/**
 * Prevents MySQL from having issues with storing URI.
 *
 * @author sky
 */
public class URIAttributeConverter implements AttributeConverter<URI, String> {
    /**
     * Converts URI to String.
     *
     * @param uri URI instance.
     * @return Converted String.
     */

    @Override
    public String convertToDatabaseColumn(URI uri) {
        return (uri != null) ? uri.toString() : null;
    }

    /**
     * Converts String to URI.
     *
     * @param uri String to be converted.
     * @return Converted URI.
     */
    @Override
    public URI convertToEntityAttribute(String uri) {
        return (uri != null) ? URI.create(uri) : null;
    }
}