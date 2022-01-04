package ws.furrify.sources.providers.deviantart;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import ws.furrify.sources.providers.deviantart.dto.DeviantArtDeviationQueryDTO;
import ws.furrify.sources.providers.deviantart.dto.DeviantArtUserQueryDTO;

/**
 * Communication interface with DeviantArt API.
 *
 * @author Skyte
 */
public interface DeviantArtServiceClient {
    /**
     * Get DeviantArt deviation.
     *
     * @param bearerToken Bearer token to use for authentication.
     * @param deviationId Deviation id.
     * @return Deviation query dto.
     */
    @Headers("Authorization: {bearerToken}")
    @RequestLine("GET /deviation/{deviationId}")
    DeviantArtDeviationQueryDTO getDeviation(@Param("bearerToken") String bearerToken, @Param("deviationId") String deviationId);

    /**
     * Get DeviantArt user.
     *
     * @param bearerToken Bearer token to use for authentication.
     * @param username    Username.
     * @return User query dto.
     */
    @Headers("Authorization: {bearerToken}")
    @RequestLine("GET /user/profile/{username}")
    DeviantArtUserQueryDTO getUser(@Param("bearerToken") String bearerToken, @Param("username") String username);
}