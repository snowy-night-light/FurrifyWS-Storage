package ws.furrify.posts.media;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import lombok.extern.java.Log;
import ws.furrify.posts.media.vo.MediaFile;
import ws.furrify.posts.media.vo.MediaPriority;
import ws.furrify.posts.media.vo.MediaSource;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
@ToString
@Log
class Media {
    private final Long id;
    @NonNull
    private final UUID mediaId;
    @NonNull
    private final UUID postId;
    @NonNull
    private final UUID ownerId;
    @NonNull
    private MediaPriority priority;
    @NonNull
    private MediaFile file;
    @NonNull
    private Set<MediaSource> sources;

    private final ZonedDateTime createDate;

    static Media restore(MediaSnapshot mediaSnapshot) {
        return new Media(
                mediaSnapshot.getId(),
                mediaSnapshot.getMediaId(),
                mediaSnapshot.getPostId(),
                mediaSnapshot.getOwnerId(),
                MediaPriority.of(mediaSnapshot.getPriority()),
                MediaFile.builder()
                        .extension(mediaSnapshot.getExtension())
                        .thumbnailUri(mediaSnapshot.getThumbnailUri())
                        .filename(mediaSnapshot.getFilename())
                        .md5(mediaSnapshot.getMd5())
                        .fileUri(mediaSnapshot.getFileUri())
                        .build(),
                new HashSet<>(mediaSnapshot.getSources()),
                mediaSnapshot.getCreateDate()
        );
    }

    MediaSnapshot getSnapshot() {
        return MediaSnapshot.builder()
                .id(id)
                .mediaId(mediaId)
                .postId(postId)
                .ownerId(ownerId)
                .priority(priority.getPriority())
                .extension(file.getExtension())
                .thumbnailUri(file.getThumbnailUri())
                .filename(file.getFilename())
                .md5(file.getMd5())
                .fileUri(file.getFileUri())
                .sources(sources.stream().collect(Collectors.toUnmodifiableSet()))
                .createDate(createDate)
                .build();
    }

    void replacePriority(@NonNull final MediaPriority mediaPriority) {
        this.priority = mediaPriority;
    }

    void addSource(@NonNull final MediaSource artistSource) {
        this.sources.add(artistSource);
    }

    void deleteSource(final UUID sourceId) {
        this.sources = sources.stream()
                .filter(source -> !source.getSourceId().equals(sourceId))
                .collect(Collectors.toSet());
    }

    void updateSourceDataInSources(@NonNull final MediaSource artistSource) {
        // Filter sourceSet to find if source exists by sourceId.
        this.sources.stream()
                .filter(source -> source.getSourceId().equals(artistSource.getSourceId()))
                .findAny()
                .orElseThrow(() -> {
                    log.severe("Original source [sourceId=" + artistSource.getSourceId() + "] was not found.");

                    return new IllegalStateException("Original sourceId was not found.");
                });

        // Filter sourceSet to get all without old source
        Set<MediaSource> filteredSourceSet = this.sources.stream()
                .filter(source -> !source.getSourceId().equals(artistSource.getSourceId()))
                .collect(Collectors.toSet());
        // Add updated source to sourceSet
        filteredSourceSet.add(artistSource);

        this.sources = filteredSourceSet;
    }

    void replaceMediaFile(@NonNull final MediaFile file) {
        this.file = file;
    }
}