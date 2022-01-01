package ws.furrify.sources.providers.deviantart;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import ws.furrify.sources.providers.deviantart.dto.DeviantArtDeviationQueryDTO;

/**
 * Communication interface with DeviantArt API.
 *
 * @author Skyte
 */
public interface DeviantArtServiceClient {
    /**
     * Get DeviantArt deviation.
     *
     * @param deviationId Deviation id.
     * @return Deviation query dto.
     */
    @Headers("Authorization: {bearerToken}")
    @RequestLine("GET /deviation/{deviationId}")
    DeviantArtDeviationQueryDTO getDeviation(@Param("bearerToken") String bearerToken, @Param("deviationId") String deviationId);
}