package ws.furrify.artists.artist.dto;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import ws.furrify.artists.artist.ArtistEvent;
import ws.furrify.artists.artist.ArtistQueryRepository;
import ws.furrify.artists.artist.vo.ArtistAvatar;
import ws.furrify.artists.artist.vo.ArtistSource;

import java.net.URI;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Creates ArtistDTO from ArtistEvent.
 * `
 *
 * @author Skyte
 */
@RequiredArgsConstructor
public class ArtistDtoFactory {

    private final ArtistQueryRepository artistQueryRepository;

    @SneakyThrows
    public ArtistDTO from(UUID key, ArtistEvent artistEvent) {
        Instant createDateInstant = artistEvent.getData().getCreateDate();
        ZonedDateTime createDate = null;

        if (createDateInstant != null) {
            createDate = createDateInstant.atZone(ZoneId.systemDefault());
        }

        var artistId = UUID.fromString(artistEvent.getArtistId());

        return ArtistDTO.builder()
                .id(
                        artistQueryRepository.getIdByArtistId(artistId)
                )
                .artistId(artistId)
                .ownerId(key)
                .nicknames(new HashSet<>(artistEvent.getData().getNicknames()))
                .preferredNickname(artistEvent.getData().getPreferredNickname())
                .sources(
                        artistEvent.getData().getSources().stream()
                                .map(artistSourceEvent ->
                                        new ArtistSource(
                                                UUID.fromString(artistSourceEvent.getSourceId()),
                                                artistSourceEvent.getStrategy(),
                                                artistSourceEvent.getData()
                                        )
                                ).collect(Collectors.toSet())
                )
                .avatar(
                        (artistEvent.getData().getAvatar() == null) ? null :
                                ArtistAvatar.builder()
                                        .avatarId(
                                                UUID.fromString(artistEvent.getData().getAvatar().getAvatarId())
                                        )
                                        .fileUri(new URI(artistEvent.getData().getAvatar().getFileUri()))
                                        .thumbnailUri(new URI(artistEvent.getData().getAvatar().getThumbnailUri()))
                                        .extension(artistEvent.getData().getAvatar().getExtension())
                                        .build()
                )
                .createDate(createDate)
                .build();
    }

}
