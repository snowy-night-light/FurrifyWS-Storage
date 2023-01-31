package ws.furrify.sources.converter;

import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;
import ws.furrify.sources.source.converter.SourceStrategyAttributeConverter;

@Converter(autoApply = true)
@Component
class SourceStrategyAttributeConverterImpl extends SourceStrategyAttributeConverter {
}
