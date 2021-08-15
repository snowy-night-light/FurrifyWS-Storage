package ws.furrify.posts.attachment.dto.command;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.Value;
import ws.furrify.posts.attachment.dto.AttachmentDTO;
import ws.furrify.shared.dto.CommandDTO;

/**
 * @author Skyte
 */
@Value
@AllArgsConstructor(onConstructor_ = @JsonCreator)
@ToString
public class AttachmentReplaceCommandDTO implements CommandDTO<AttachmentDTO> {

    @Override
    public AttachmentDTO toDTO() {
        return AttachmentDTO.builder()
                .build();
    }
}
