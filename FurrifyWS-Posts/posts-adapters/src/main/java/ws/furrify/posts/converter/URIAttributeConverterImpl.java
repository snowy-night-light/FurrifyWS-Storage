package ws.furrify.posts.converter;

import jakarta.persistence.Converter;
import ws.furrify.shared.converter.URIAttributeConverter;

@Converter(autoApply = true)
public class URIAttributeConverterImpl extends URIAttributeConverter {
}
