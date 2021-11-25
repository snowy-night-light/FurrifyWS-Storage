package ws.furrify.posts.attachment;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import lombok.extern.java.Log;
import ws.furrify.posts.attachment.vo.AttachmentFile;
import ws.furrify.posts.attachment.vo.AttachmentSource;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
@ToString
@Log
class Attachment {
    private final Long id;
    @NonNull
    private final UUID attachmentId;
    @NonNull
    private final UUID postId;
    @NonNull
    private final UUID ownerId;
    @NonNull
    private final AttachmentFile file;
    @NonNull
    private Set<AttachmentSource> sources;

    private final ZonedDateTime createDate;

    static Attachment restore(AttachmentSnapshot attachmentSnapshot) {
        return new Attachment(
                attachmentSnapshot.getId(),
                attachmentSnapshot.getAttachmentId(),
                attachmentSnapshot.getPostId(),
                attachmentSnapshot.getOwnerId(),
                AttachmentFile.builder()
                        .extension(attachmentSnapshot.getExtension())
                        .filename(attachmentSnapshot.getFilename())
                        .md5(attachmentSnapshot.getMd5())
                        .fileUri(attachmentSnapshot.getFileUri())
                        .build(),
                new HashSet<>(attachmentSnapshot.getSources()),
                attachmentSnapshot.getCreateDate()
        );
    }

    AttachmentSnapshot getSnapshot() {
        return AttachmentSnapshot.builder()
                .id(id)
                .attachmentId(attachmentId)
                .postId(postId)
                .ownerId(ownerId)
                .extension(file.getExtension())
                .filename(file.getFilename())
                .md5(file.getMd5())
                .fileUri(file.getFileUri())
                .sources(sources.stream().collect(Collectors.toUnmodifiableSet()))
                .createDate(createDate)
                .build();
    }

    void addSource(@NonNull final AttachmentSource artistSource) {
        this.sources.add(artistSource);
    }

    void deleteSource(final UUID sourceId) {
        this.sources = sources.stream()
                .filter(source -> !source.getSourceId().equals(sourceId))
                .collect(Collectors.toSet());
    }

    void updateSourceDataInSources(@NonNull final AttachmentSource artistSource) {
        // Filter sourceSet to find if source exists by sourceId.
        this.sources.stream()
                .filter(source -> source.getSourceId().equals(artistSource.getSourceId()))
                .findAny()
                .orElseThrow(() -> {
                    log.severe("Original source [sourceId=" + artistSource.getSourceId() + "] was not found.");

                    return new IllegalStateException("Original sourceId was not found.");
                });

        // Filter sourceSet to get all without old source
        Set<AttachmentSource> filteredSourceSet = this.sources.stream()
                .filter(source -> !source.getSourceId().equals(artistSource.getSourceId()))
                .collect(Collectors.toSet());
        // Add updated source to sourceSet
        filteredSourceSet.add(artistSource);

        this.sources = filteredSourceSet;
    }
}