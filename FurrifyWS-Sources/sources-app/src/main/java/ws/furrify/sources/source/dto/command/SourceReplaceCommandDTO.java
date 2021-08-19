package ws.furrify.sources.source.dto.command;


import lombok.ToString;
import lombok.Value;
import ws.furrify.shared.dto.CommandDTO;
import ws.furrify.sources.source.dto.SourceDTO;

/**
 * @author Skyte
 */
@Value
@ToString
public class SourceReplaceCommandDTO implements CommandDTO<SourceDTO> {

    @Override
    public SourceDTO toDTO() {
        return SourceDTO.builder()
                .build();
    }
}
