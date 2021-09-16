package ws.furrify.sources.source.converter;

import lombok.SneakyThrows;
import lombok.extern.java.Log;
import ws.furrify.shared.exception.Errors;
import ws.furrify.sources.source.strategy.SourceStrategy;

import javax.persistence.AttributeConverter;
import java.lang.reflect.InvocationTargetException;

/**
 * Converts source strategy to database table.
 *
 * @author sky
 */
@Log
public class SourceStrategyAttributeConverter implements AttributeConverter<SourceStrategy, String> {

    private final static String STRATEGY_PACKAGE = "ws.furrify.sources.source.strategy";

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

            throw new IllegalStateException(Errors.MISSING_STRATEGY.getErrorMessage(className));
        }
    }
}