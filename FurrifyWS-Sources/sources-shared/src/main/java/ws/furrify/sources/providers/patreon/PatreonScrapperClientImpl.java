package ws.furrify.sources.providers.patreon;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Arrays;

/**
 * Implementation of Patreon scrapper client using JSoup.
 *
 * @author Skyte
 */
public class PatreonScrapperClientImpl implements PatreonScrapperClient {

    private final static String CAMPAIGN_IMAGE_PROPERTY = "og:image";
    private final static String CAMPAIGN_IN_IMAGE_URL = "campaign";

    @Override
    public int getCampaignId(final String url) throws IOException {
        Document document = Jsoup.connect(url)
                .userAgent("Mozilla/5.0")
                .header("Accept", "text/html")
                .header("Accept-Encoding", "gzip,deflate")
                .header("Accept-Language", "it-IT,en;q=0.8,en-US;q=0.6,de;q=0.4,it;q=0.2,es;q=0.2")
                .header("Connection", "keep-alive")
                .ignoreContentType(true)
                .get();

        Elements metaTags = document.head().getElementsByTag("meta");

        for (Element metaTag : metaTags) {
            String propertyTag = metaTag.attr("property");
            if (propertyTag.equals(CAMPAIGN_IMAGE_PROPERTY)) {
                // Get image url and split it into chunks by "/"
                String[] imageUrl = metaTag.attr("content").split("/");
                int campaignIdPositionInUrl = -1;

                for (int i = 0; i <= imageUrl.length - 1; i++) {
                    // Search for 'campaign' in image url
                    if (imageUrl[i].equals(CAMPAIGN_IN_IMAGE_URL)) {
                        // Save campaign id position in array
                        campaignIdPositionInUrl = i + 1;

                        break;
                    }
                }

                // If campaign id was not found
                if (campaignIdPositionInUrl == -1) {
                    throw new IOException("Campaign not found.");
                }

                return Integer.parseInt(imageUrl[campaignIdPositionInUrl]);
            } else {
                throw new IOException("Campaign not found.");
            }

        }

        // If meta tag not found
        throw new IOException("Campaign not found.");
    }

}
