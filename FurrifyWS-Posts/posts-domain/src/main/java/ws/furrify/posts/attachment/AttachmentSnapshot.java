package ws.furrify.posts.attachment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ws.furrify.posts.attachment.vo.AttachmentSource;

import java.net.URL;
import java.time.ZonedDateTime;
import java.util.Set;
import java.util.UUID;

import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@EqualsAndHashCode
@ToString
@Getter(value = PACKAGE)
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
@Builder(access = PACKAGE)
class AttachmentSnapshot {
    private Long id;

    private UUID attachmentId;
    private UUID postId;
    private UUID ownerId;

    private String filename;
    private String md5;

    private AttachmentExtension extension;

    private URL fileUrl;

    private Set<AttachmentSource> sources;

    private ZonedDateTime createDate;
}
