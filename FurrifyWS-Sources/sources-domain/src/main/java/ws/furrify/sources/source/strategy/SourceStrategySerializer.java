package ws.furrify.sources.source.strategy;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

/**
 * @author sky
 */
public class SourceStrategySerializer extends StdSerializer<SourceStrategy> {

    public SourceStrategySerializer() {
        super(SourceStrategy.class);
    }

    private SourceStrategySerializer(Class<SourceStrategy> t) {
        super(t);
    }

    @Override
    public void serialize(SourceStrategy strategy, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeObject(strategy.getClass().getSimpleName());
    }
}