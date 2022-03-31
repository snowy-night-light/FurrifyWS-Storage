package ws.furrify.sources.providers.deviantart;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URI;

/**
 * Implementation of DeviantArt scrapper client using JSoup.
 *
 * @author Skyte
 */
public class DeviantArtScrapperClientImpl implements DeviantArtScrapperClient {

    private final static String DEVIANT_ART_APP_URL_PREFIX = "DeviantArt://deviation/";
    private final static String DEVIANT_ART_APP_URL_PROPERTY_VALUE = "da:appurl";

    @Override
    public String scrapDeviationId(final String url) throws IOException {
        Document document = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.4844.51 Safari/537.36")
                .get();
        Elements metaTags = document.getElementsByTag("meta");

        for (Element metaTag : metaTags) {
            String appUrl = metaTag.attr("property");

            if(DEVIANT_ART_APP_URL_PROPERTY_VALUE.equals(appUrl)) {
                String id = metaTag.attr("content")
                        .replace(DEVIANT_ART_APP_URL_PREFIX, "");

                if (id.isBlank()) {
                    throw new IOException("Deviation was not found.");
                }

                return id;
            }
        }

        return null;
    }
}
