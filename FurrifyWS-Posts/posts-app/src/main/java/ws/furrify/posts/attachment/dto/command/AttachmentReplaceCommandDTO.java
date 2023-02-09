package ws.furrify.posts.attachment.dto.command;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.Value;
import ws.furrify.posts.attachment.AttachmentExtension;
import ws.furrify.posts.attachment.dto.AttachmentDTO;
import ws.furrify.shared.dto.CommandDTO;

/**
 * @author Skyte
 */
@Value
@AllArgsConstructor(onConstructor_ = @JsonCreator)
@ToString
public class AttachmentReplaceCommandDTO implements CommandDTO<AttachmentDTO> {

    @NotNull
    AttachmentExtension extension;

    @Override
    public AttachmentDTO toDTO() {
        return AttachmentDTO.builder()
                .extension(extension)
                .build();
    }
}
