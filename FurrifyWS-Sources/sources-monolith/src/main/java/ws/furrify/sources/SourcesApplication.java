package ws.furrify.sources;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;

/**
 * @author Skyte
 */
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
// FIX Compatibility
@ImportAutoConfiguration({FeignAutoConfiguration.class})
public class SourcesApplication {

	public static void main(String[] args) {
		SpringApplication.run(SourcesApplication.class, args);
	}

}
