package ws.furrify.posts.converter;

import jakarta.persistence.Converter;
import ws.furrify.shared.converter.ZonedDateTimeAttributeConverter;

@Converter(autoApply = true)
public class ZonedDateTimeAttributeConverterImpl extends ZonedDateTimeAttributeConverter {
}
