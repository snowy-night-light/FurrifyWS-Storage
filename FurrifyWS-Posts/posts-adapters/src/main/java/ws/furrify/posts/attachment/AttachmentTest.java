package ws.furrify.posts.attachment;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import ws.furrify.posts.attachment.dto.AttachmentDTO;
import ws.furrify.posts.attachment.vo.AttachmentSource;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

@Component
@RequiredArgsConstructor
class AttachmentTest implements CommandLineRunner {

    private final SqlAttachmentRepository sqlAttachmentRepository;
    private final Environment environment;

    @Override
    public void run(final String... args) {
        Arrays.stream(environment.getActiveProfiles())
                .filter("dev"::equals)
                .findAny()
                .ifPresent((profile) -> createTestingAttachments());
    }

    @SneakyThrows
    private void createTestingAttachments() {
        var attachmentFactory = new AttachmentFactory();

        var userId = UUID.fromString("f08e6027-b997-452d-85a6-0cf2d5a1741e");
        var postId = UUID.fromString("7c2c35f3-20e9-4b7e-a455-253b7b78e2fa");
        var attachmentId = UUID.fromString("566548cf-fb1d-4552-a880-c741a1eb9d0e");
        var attachmentSourceId = UUID.fromString("87a5d0b2-bba8-4e94-b7d3-c9ad51431dd5");

        sqlAttachmentRepository.save(
                attachmentFactory.from(
                        AttachmentDTO.builder()
                                .attachmentId(attachmentId)
                                .postId(postId)
                                .ownerId(userId)
                                .extension(AttachmentExtension.PSD)
                                .fileUrl(new URI("/test"))
                                .filename("yes.psd")
                                .md5("3c518eeb674c71b30297f072fde7eba5")
                                .sources(Collections.singleton(
                                        new AttachmentSource(
                                                attachmentSourceId,
                                                "DeviantArtV1SourceStrategy",
                                                new HashMap<>(1) {{
                                                    put("id", "2662");
                                                }}
                                        )
                                ))
                                .createDate(ZonedDateTime.now())
                                .build()
                ).getSnapshot()
        );

        System.out.println("AttachmentId: " + attachmentId);
    }

}
