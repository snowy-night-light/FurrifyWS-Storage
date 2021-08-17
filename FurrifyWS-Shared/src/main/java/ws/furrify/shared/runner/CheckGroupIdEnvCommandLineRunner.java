package ws.furrify.shared.runner;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;

/**
 * Checks for KAFKA_CONSUMER_GROUP_ID env variable and logs if not present.
 *
 * @author sky
 */
@Slf4j
public class CheckGroupIdEnvCommandLineRunner implements CommandLineRunner {

    @Value("${KAFKA_CONSUMER_GROUP_ID:}")
    private String groupIdRegion;

    @Override
    public void run(final String... args) throws Exception {
        if (groupIdRegion.isBlank()) {
            log.error("Env KAFKA_CONSUMER_GROUP_ID variable is not set! Kafka consumers will load balance.");
        }
    }
}
