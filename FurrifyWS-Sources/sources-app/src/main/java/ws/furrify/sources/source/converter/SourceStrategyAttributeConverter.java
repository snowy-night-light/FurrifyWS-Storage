package ws.furrify.sources.source.converter;

import lombok.SneakyThrows;
import lombok.extern.java.Log;
import ws.furrify.shared.exception.Errors;
import ws.furrify.sources.source.strategy.SourceStrategy;

import javax.persistence.AttributeConverter;
import java.lang.reflect.InvocationTargetException;

/**
 * Converts source strategy to database table.
 */
@Log
public class SourceStrategyAttributeConverter implements AttributeConverter<SourceStrategy, String> {

    private final static String STRATEGY_PACKAGE = "ws.furrify.sources.source.strategy";
    private final static String DEFAULT_STRATEGY_PATH = "ws.furrify.sources.source.strategy.DefaultSourceStrategy";

    @Override
    public String convertToDatabaseColumn(final SourceStrategy sourceStrategy) {
        return sourceStrategy.getClass().getSimpleName();
    }

    @SneakyThrows
    @Override
    public SourceStrategy convertToEntityAttribute(final String className) {
        try {
            return (SourceStrategy) Class.forName(STRATEGY_PACKAGE + "." + className).getConstructor().newInstance();
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            log.severe(Errors.MISSING_STRATEGY.getErrorMessage(className));

            return (SourceStrategy) Class.forName(DEFAULT_STRATEGY_PATH).getConstructor().newInstance();
        }
    }
}