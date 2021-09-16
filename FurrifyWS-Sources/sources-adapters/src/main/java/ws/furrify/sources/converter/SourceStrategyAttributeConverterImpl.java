package ws.furrify.sources.converter;

import org.springframework.stereotype.Component;
import ws.furrify.sources.source.converter.SourceStrategyAttributeConverter;

import javax.persistence.Converter;

@Converter(autoApply = true)
@Component
class SourceStrategyAttributeConverterImpl extends SourceStrategyAttributeConverter {
}
