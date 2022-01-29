package ws.furrify.artists.converter;

import ws.furrify.shared.converter.UUIDAttributeConverter;

import javax.persistence.Converter;

@Converter(autoApply = true)
public class UUIDAttributeConverterImpl extends UUIDAttributeConverter {
}
