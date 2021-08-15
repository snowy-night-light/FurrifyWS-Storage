package ws.furrify.posts.attachment;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ws.furrify.posts.attachment.dto.AttachmentDtoFactory;
import ws.furrify.posts.kafka.KafkaTopicEventPublisher;

@Configuration
@RequiredArgsConstructor
class AttachmentConfig {

    private final AttachmentRepositoryImpl attachmentRepository;
    private final AttachmentQueryRepository attachmentQueryRepository;
    private final KafkaTopicEventPublisher<AttachmentEvent> eventPublisher;

    @Bean
    AttachmentFacade attachmentFacade() {
        var attachmentFactory = new AttachmentFactory();
        var attachmentDtoFactory = new AttachmentDtoFactory(attachmentQueryRepository);

        return new AttachmentFacade(
                new CreateAttachmentAdapter(attachmentFactory, eventPublisher),
                new DeleteAttachmentAdapter(eventPublisher, attachmentRepository),
                new UpdateAttachmentAdapter(eventPublisher, attachmentRepository),
                new ReplaceAttachmentAdapter(eventPublisher, attachmentRepository),
                attachmentRepository,
                attachmentFactory,
                attachmentDtoFactory
        );
    }
}
