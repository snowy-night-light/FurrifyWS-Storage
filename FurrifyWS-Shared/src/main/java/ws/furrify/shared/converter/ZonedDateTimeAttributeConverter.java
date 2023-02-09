package ws.furrify.shared.converter;

import jakarta.persistence.AttributeConverter;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Prevents MySQL from having issues with storing ZonedDateTime.
 *
 * @author sky
 */
public class ZonedDateTimeAttributeConverter implements AttributeConverter<ZonedDateTime, Timestamp> {

    /**
     * Converts ZonedDateTime to Timestamp.
     *
     * @param zonedDateTime ZonedDateTime to be converted.
     * @return Converted TimeStamp.
     */
    @Override
    public Timestamp convertToDatabaseColumn(ZonedDateTime zonedDateTime) {
        return zonedDateTime == null ? null : Timestamp.valueOf(zonedDateTime.toLocalDateTime());
    }

    /**
     * Converts Timestamp to LocalDateTime.
     *
     * @param timestamp TimeStamp to be converted.
     * @return Converted LocalDateTime.
     */
    @Override
    public ZonedDateTime convertToEntityAttribute(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime().atZone(ZoneId.systemDefault());
    }
}