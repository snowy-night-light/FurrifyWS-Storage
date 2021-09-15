package ws.furrify.sources.source.strategy;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.exception.StrategyNotFoundException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * @author sky
 */
public class SourceStrategyDeserializer extends StdDeserializer<SourceStrategy> {

    private final static String STRATEGY_PACKAGE = "ws.furrify.sources.source.strategy";

    public SourceStrategyDeserializer() {
        super(SourceStrategy.class);
    }

    private SourceStrategyDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public SourceStrategy deserialize(JsonParser jsonParser,
                                      DeserializationContext deserializationContext) throws IOException {
        JsonNode jsonNode = jsonParser.getCodec().readTree(jsonParser);

        String strategyString = jsonNode.asText();

        SourceStrategy strategy;

        try {
            strategy = (SourceStrategy) Class.forName(STRATEGY_PACKAGE + "." + strategyString).getConstructor().newInstance();
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new StrategyNotFoundException(Errors.STRATEGY_NOT_FOUND.getErrorMessage(strategyString));
        }

        return strategy;
    }
}