package ws.furrify.artists.kafka;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.retry.annotation.EnableRetry;
import ws.furrify.artists.artist.ArtistEvent;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableRetry
class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.schema-registry-servers}")
    private String schemaRegistryServers;

    @Value("${spring.kafka.consumer.group-id.prefix}")
    private String groupIdPrefix;

    @Value("${KAFKA_CONSUMER_GROUP_ID:}")
    private String groupIdRegion;

    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> properties = new HashMap<>(7);
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, groupIdPrefix + groupIdRegion);
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        properties.put("schema.registry.url", schemaRegistryServers);
        properties.put("specific.avro.reader", true);

        return properties;
    }

    @Bean
    public ConsumerFactory<String, ArtistEvent> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs());
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, ArtistEvent>> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ArtistEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());

        return factory;
    }
}