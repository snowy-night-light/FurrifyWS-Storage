package ws.furrify.posts.attachment.vo;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import java.util.Map;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

/**
 * @author Skyte
 */
@Data
@Setter(value = PRIVATE)
@AllArgsConstructor
@Builder
@NoArgsConstructor(access = PROTECTED)
public class AttachmentSource {
    @JsonIgnore
    private Long id;

    @NonNull
    private UUID sourceId;

    @NonNull
    private String strategy;

    @NonNull
    private Map<String, String> data;

    public AttachmentSource(@NonNull final UUID sourceId, @NonNull final String strategy, @NonNull final Map<String, String> data) {
        this.sourceId = sourceId;
        this.strategy = strategy;
        this.data = data;
    }
}