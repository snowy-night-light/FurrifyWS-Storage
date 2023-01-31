package ws.furrify.artists.converter;

import jakarta.persistence.Converter;
import ws.furrify.shared.converter.UUIDAttributeConverter;

@Converter(autoApply = true)
public class UUIDAttributeConverterImpl extends UUIDAttributeConverter {
}
