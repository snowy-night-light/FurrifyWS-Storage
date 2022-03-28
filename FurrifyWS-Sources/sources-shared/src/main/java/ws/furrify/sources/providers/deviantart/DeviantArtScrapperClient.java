package ws.furrify.sources.providers.deviantart;
import java.io.IOException;
import java.net.URI;

/**
 * Scrapper interface for DeviantArt site.
 *
 * @author Skyte
 */
public interface DeviantArtScrapperClient {
    /**
     * Get DeviantArt deviation id from deviation url.
     *
     * @param url Url with deviation.
     * @return Deviation id scrapped from deviation site.
     */
    String scrapDeviationId(String url) throws IOException;
}