package ws.furrify.sources.providers.patreon;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import ws.furrify.sources.providers.deviantart.dto.DeviantArtDeviationQueryDTO;
import ws.furrify.sources.providers.deviantart.dto.DeviantArtUserQueryDTO;
import ws.furrify.sources.providers.patreon.dto.PatreonCampaignQueryDTO;
import ws.furrify.sources.providers.patreon.dto.PatreonPostQueryDTO;

/**
 * Communication interface with Patreon API.
 *
 * @author Skyte
 */
public interface PatreonServiceClient {
    /**
     * Get campaign.
     *
     * @param bearerToken Bearer token to use for authentication.
     * @param campaignId  Campaign id.
     * @return Campaign query dto.
     */
    @Headers("Authorization: {bearerToken}")
    @RequestLine("GET /campaigns/{campaignId}")
    PatreonCampaignQueryDTO getCampaign(@Param("bearerToken") String bearerToken,
                                        @Param("campaignId") int campaignId);

    /**
     * Get post.
     *
     * @param bearerToken Bearer token to use for authentication.
     * @param postId      Post id.
     * @return Post query dto.
     */
    @Headers("Authorization: {bearerToken}")
    @RequestLine("GET /posts/{postId}")
    PatreonPostQueryDTO getPost(@Param("bearerToken") String bearerToken, @Param("postId") int postId);
}