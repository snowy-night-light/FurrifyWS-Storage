package ws.furrify.posts.media;

import ws.furrify.posts.media.dto.MediaDTO;

import java.time.ZonedDateTime;
import java.util.UUID;

final class MediaFactory {

    Media from(MediaDTO mediaDTO) {
        MediaSnapshot mediaSnapshot = MediaSnapshot.builder()
                .id(mediaDTO.getId())
                .mediaId(
                        (mediaDTO.getMediaId() != null) ? mediaDTO.getMediaId() : UUID.randomUUID()
                )
                .postId(mediaDTO.getPostId())
                .ownerId(mediaDTO.getOwnerId())
                .priority(
                        (mediaDTO.getPriority() != null) ? mediaDTO.getPriority() : 0
                )
                .filename(mediaDTO.getFilename())
                .md5(mediaDTO.getMd5())
                .extension(mediaDTO.getExtension())
                .fileUrl(mediaDTO.getFileUrl())
                .thumbnailUrl(mediaDTO.getThumbnailUrl())
                .createDate(
                        (mediaDTO.getCreateDate() != null) ? mediaDTO.getCreateDate() : ZonedDateTime.now()
                )
                .build();

        return Media.restore(mediaSnapshot);
    }

}
