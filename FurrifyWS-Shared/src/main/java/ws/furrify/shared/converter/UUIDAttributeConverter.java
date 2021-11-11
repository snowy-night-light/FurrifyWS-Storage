package ws.furrify.shared.converter;

import javax.persistence.AttributeConverter;
import java.util.UUID;

/**
 * Prevents MySQL from having issues with storing UUID.
 *
 * @author sky
 */
public class UUIDAttributeConverter implements AttributeConverter<UUID, String> {
    /**
     * Converts UUID to String.
     *
     * @param uuid Public id.
     * @return Converted String.
     */

    @Override
    public String convertToDatabaseColumn(UUID uuid) {
        return (uuid != null) ? uuid.toString() : null;
    }

    /**
     * Converts String to UUID.
     *
     * @param uuid String to be converted.
     * @return Converted UUID.
     */
    @Override
    public UUID convertToEntityAttribute(String uuid) {
        return (uuid != null) ? UUID.fromString(uuid) : null;
    }
}