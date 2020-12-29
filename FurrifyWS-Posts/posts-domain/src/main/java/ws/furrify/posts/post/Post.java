package ws.furrify.posts.post;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.ZonedDateTime;
import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
@ToString
class Post {
    private final Long id;
    private final UUID postId;
    private final UUID ownerId;
    private String title;
    private String description;
    private final ZonedDateTime createDate;

    static Post restore(PostSnapshot postSnapshot) {
        return new Post(
                postSnapshot.getId(),
                postSnapshot.getPostId(),
                postSnapshot.getOwnerId(),
                postSnapshot.getTitle(),
                postSnapshot.getDescription(),
                postSnapshot.getCreateDate()
        );
    }

    PostSnapshot getSnapshot() {
        return new PostSnapshot(
                id,
                postId,
                ownerId,
                title,
                description,
                createDate
        );
    }

    void updateDetails(final String newTitle,
                       final String newDescription) {
        this.title = newTitle;
        this.description = newDescription;
    }
}