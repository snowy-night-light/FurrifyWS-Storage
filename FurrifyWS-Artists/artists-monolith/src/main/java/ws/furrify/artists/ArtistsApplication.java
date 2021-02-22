package ws.furrify.artists;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author Skyte
 */
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
public class ArtistsApplication {

	public static void main(String[] args) {
		SpringApplication.run(ArtistsApplication.class, args);
	}

}
