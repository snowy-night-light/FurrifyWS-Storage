package ws.furrify.posts.attachment;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import ws.furrify.posts.attachment.dto.AttachmentDTO;

import java.net.URL;
import java.time.ZonedDateTime;
import java.util.Arrays;
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

        var userId = UUID.fromString("82722f67-ec52-461f-8294-158d8affe7a3");
        var postId = UUID.fromString("7c2c35f3-20e9-4b7e-a455-253b7b78e2fa");
        var attachmentId = UUID.fromString("566548cf-fb1d-4552-a880-c741a1eb9d0e");

        sqlAttachmentRepository.save(
                attachmentFactory.from(
                        AttachmentDTO.builder()
                                .attachmentId(attachmentId)
                                .postId(postId)
                                .ownerId(userId)
                                .extension(AttachmentExtension.PSD)
                                .fileUrl(new URL("https://example.com/"))
                                .filename("yes.psd")
                                .md5("3c518eeb674c71b30297f072fde7eba5")
                                .createDate(ZonedDateTime.now())
                                .build()
                ).getSnapshot()
        );

        System.out.println("AttachmentId: " + attachmentId);
    }

}
