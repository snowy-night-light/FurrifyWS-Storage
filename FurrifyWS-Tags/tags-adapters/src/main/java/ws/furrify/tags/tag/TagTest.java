package ws.furrify.tags.tag;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import ws.furrify.tags.tag.dto.TagDTO;
import ws.furrify.tags.tag.vo.TagType;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.UUID;

@Component
@RequiredArgsConstructor
class TagTest implements CommandLineRunner {

    private final SqlTagRepository sqlTagRepository;
    private final Environment environment;

    @Override
    public void run(final String... args) {
        Arrays.stream(environment.getActiveProfiles())
                .filter("dev"::equals)
                .findAny()
                .ifPresent((profile) -> createTestingPosts());
    }

    private void createTestingPosts() {
        var tagFactory = new TagFactory();

        var userId = UUID.fromString("bd08e777-6da3-4d46-8b9e-501e837a4462");
        var tagValue = "walking";

        sqlTagRepository.save(
                tagFactory.from(
                        TagDTO.builder()
                                .value(tagValue)
                                .ownerId(userId)
                                .type(TagType.ACTION)
                                .createDate(ZonedDateTime.now())
                                .build()
                ).getSnapshot()
        );

        System.out.println("Tag value: " + tagValue);
    }

}
