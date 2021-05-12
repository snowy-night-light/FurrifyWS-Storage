package ws.furrify.posts.post;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import lombok.extern.java.Log;
import ws.furrify.posts.post.vo.PostArtist;
import ws.furrify.posts.post.vo.PostDescription;
import ws.furrify.posts.post.vo.PostMedia;
import ws.furrify.posts.post.vo.PostTag;
import ws.furrify.posts.post.vo.PostTitle;

import java.net.URL;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
@ToString
@Log
class Post {
    private final Long id;
    @NonNull
    private final UUID postId;
    @NonNull
    private final UUID ownerId;
    @NonNull
    private PostTitle title;
    @NonNull
    private PostDescription description;
    @NonNull
    private Set<PostTag> tags;
    @NonNull
    private Set<PostArtist> artists;
    @NonNull
    private Set<PostMedia> mediaSet;

    private final ZonedDateTime createDate;

    static Post restore(PostSnapshot postSnapshot) {
        return new Post(
                postSnapshot.getId(),
                postSnapshot.getPostId(),
                postSnapshot.getOwnerId(),
                PostTitle.of(
                        postSnapshot.getTitle()
                ),
                PostDescription.of(
                        postSnapshot.getDescription()
                ),
                new HashSet<>(postSnapshot.getTags()),
                new HashSet<>(postSnapshot.getArtists()),
                new HashSet<>(postSnapshot.getMediaSet()),
                postSnapshot.getCreateDate()
        );
    }

    PostSnapshot getSnapshot() {
        return PostSnapshot.builder()
                .id(id)
                .postId(postId)
                .ownerId(ownerId)
                .title(title.getTitle())
                .description(description.getDescription())
                .tags(tags.stream().collect(Collectors.toUnmodifiableSet()))
                .artists(artists.stream().collect(Collectors.toUnmodifiableSet()))
                .mediaSet(mediaSet.stream().collect(Collectors.toUnmodifiableSet()))
                .createDate(createDate)
                .build();
    }

    void updateDetails(@NonNull final PostTitle newTitle,
                       final PostDescription newDescription) {
        this.title = newTitle;
        this.description = newDescription;
    }

    void removeTag(@NonNull final String value) {
        this.tags = tags.stream()
                .filter(tag -> !tag.getValue().equals(value))
                .collect(Collectors.toSet());
    }

    void updateTagDetailsInTags(@NonNull final String originalValue,
                                @NonNull final PostTag newTag) {
        // Filter tags to find a if tag exists by original value.
        this.tags.stream()
                .filter(tag -> tag.getValue().equals(originalValue))
                .findAny()
                .orElseThrow(() -> {
                    log.severe("Original tag [value=" + originalValue + "] was not found.");

                    return new IllegalStateException("Original tag value was not found.");
                });

        // Filter tags to get all without old tag
        Set<PostTag> filteredTags = this.tags.stream()
                .filter(tag -> !tag.getValue().equals(originalValue))
                .collect(Collectors.toSet());
        // Add updated tag to tags
        filteredTags.add(newTag);

        this.tags = filteredTags;
    }

    void replaceTags(@NonNull final Set<PostTag> tags) {
        this.tags = new HashSet<>(tags);
    }

    void removeArtist(@NonNull final UUID artistId) {
        this.artists = artists.stream()
                .filter(artist -> !artist.getArtistId().equals(artistId))
                .collect(Collectors.toSet());
    }

    void updateArtistDetailsInArtists(@NonNull final UUID artistId,
                                      @NonNull final String newPreferredNickname) {
        // Filter artists to find a if artist exists by artistId.
        this.artists.stream()
                .filter(artist -> artist.getArtistId().equals(artistId))
                .findAny()
                .orElseThrow(() -> {
                    log.severe("Original artist [artistId=" + artistId + "] was not found.");

                    return new IllegalStateException("Original artistId was not found.");
                });

        // Create artist with new preferred nickname
        PostArtist postArtist = new PostArtist(artistId, newPreferredNickname);

        // Filter artists to get all without old artist
        Set<PostArtist> filteredArtists = this.artists.stream()
                .filter(artist -> !artist.getArtistId().equals(artistId))
                .collect(Collectors.toSet());
        // Add updated artists to artists
        filteredArtists.add(postArtist);

        this.artists = filteredArtists;
    }

    void replaceArtists(@NonNull final Set<PostArtist> artists) {
        this.artists = new HashSet<>(artists);
    }

    void removeMedia(final UUID mediaId) {
        this.mediaSet = mediaSet.stream()
                .filter(media -> !media.getMediaId().equals(mediaId))
                .collect(Collectors.toSet());
    }

    void updateMediaDetailsInMediaSet(@NonNull final UUID mediaId,
                                      @NonNull final Integer priority,
                                      final URL thumbnailUrl,
                                      @NonNull final String extension,
                                      @NonNull final String status) {
        // Filter mediaSet to find a if media exists by mediaId.
        this.mediaSet.stream()
                .filter(media -> media.getMediaId().equals(mediaId))
                .findAny()
                .orElseThrow(() -> {
                    log.severe("Original media [mediaId=" + mediaId + "] was not found.");

                    return new IllegalStateException("Original mediaId was not found.");
                });

        // Create post media with new details
        PostMedia postMedia = new PostMedia(
                mediaId,
                priority,
                thumbnailUrl,
                extension,
                status
        );

        // Filter mediaSet to get all without old media
        Set<PostMedia> filteredMediaSet = this.mediaSet.stream()
                .filter(media -> !media.getMediaId().equals(mediaId))
                .collect(Collectors.toSet());
        // Add updated media to mediaSet
        filteredMediaSet.add(postMedia);

        this.mediaSet = filteredMediaSet;
    }
}