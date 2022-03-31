package ws.furrify.sources.providers.patreon;
import java.io.IOException;

/**
 * Scrapper interface for Patreon site.
 *
 * @author Skyte
 */
public interface PatreonScrapperClient {
    /**
     * Check if Patreon campaign exists from url.
     *
     * @param url Url with Patreon campaign.
     * @throws IOException Patreon campaign not found.
     * @return Campaign id from url.
     */
    int getCampaignId(String url) throws IOException;
}