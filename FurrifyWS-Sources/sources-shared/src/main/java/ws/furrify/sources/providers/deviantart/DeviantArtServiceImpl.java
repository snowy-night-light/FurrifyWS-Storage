package ws.furrify.sources.providers.deviantart;

import feign.Feign;
import feign.FeignException;
import feign.Logger;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import feign.okhttp.OkHttpClient;
import feign.slf4j.Slf4jLogger;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import lombok.extern.slf4j.Slf4j;
import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.exception.ExternalProviderServerSideErrorException;
import ws.furrify.shared.exception.ExternalProviderTokenExpiredException;
import ws.furrify.shared.exception.HttpStatus;
import ws.furrify.sources.providers.deviantart.dto.DeviantArtDeviationQueryDTO;

/**
 * Implementation of DeviantArt communication service using Feign.
 *
 * @author Skyte
 */
@Slf4j
public class DeviantArtServiceImpl implements DeviantArtServiceClient {

    private final DeviantArtServiceClient deviantArtServiceClient;

    public DeviantArtServiceImpl() {
        this.deviantArtServiceClient = Feign.builder()
                .client(new OkHttpClient())
                .encoder(new GsonEncoder())
                .decoder(new GsonDecoder())
                .logger(new Slf4jLogger(DeviantArtServiceClient.class))
                .logLevel(Logger.Level.FULL)
                .target(DeviantArtServiceClient.class, "https://www.deviantart.com/api/v1/oauth2");
    }

    @Bulkhead(name = "getDeviation", fallbackMethod = "deviantArtFallback")
    @Override
    public DeviantArtDeviationQueryDTO getDeviation(final String bearerToken, final String deviationId) {
        return deviantArtServiceClient.getDeviation(bearerToken, deviationId);
    }

    private DeviantArtDeviationQueryDTO deviantArtFallback(Throwable throwable) {
        var exception = (FeignException) throwable;

        HttpStatus status = HttpStatus.of(exception.status());

        switch (status) {
            case NOT_FOUND -> {
                return null;
            }

            case INTERNAL_SERVER_ERROR -> throw new ExternalProviderServerSideErrorException(Errors.EXTERNAL_PROVIDER_SERVER_SIDE_ERROR.getErrorMessage("deviantart"));

            case UNAUTHORIZED -> throw new ExternalProviderTokenExpiredException(Errors.EXTERNAL_PROVIDER_TOKEN_HAS_EXPIRED.getErrorMessage("deviantart"));

            default -> {
                log.error("DeviantArt identity provider endpoint returned unhandled status " + status.getStatus() + ".");

                throw exception;
            }
        }
    }
}
