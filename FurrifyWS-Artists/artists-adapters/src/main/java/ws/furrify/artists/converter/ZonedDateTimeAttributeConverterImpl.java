package ws.furrify.artists.converter;

import ws.furrify.shared.converter.ZonedDateTimeAttributeConverter;

import javax.persistence.Converter;

@Converter(autoApply = true)
public class ZonedDateTimeAttributeConverterImpl extends ZonedDateTimeAttributeConverter {
}
