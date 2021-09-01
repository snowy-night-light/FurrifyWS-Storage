package ws.furrify.sources.source.dto.command;


import lombok.ToString;
import lombok.Value;
import ws.furrify.shared.dto.CommandDTO;
import ws.furrify.sources.source.dto.SourceDTO;
import ws.furrify.sources.source.strategy.SourceStrategy;

import java.util.HashMap;

/**
 * @author Skyte
 */
@Value
@ToString
public class SourceUpdateCommandDTO implements CommandDTO<SourceDTO> {

    HashMap<String, String> data;

    SourceStrategy strategy;

    @Override
    public SourceDTO toDTO() {
        return SourceDTO.builder()
                .build();
    }
}
