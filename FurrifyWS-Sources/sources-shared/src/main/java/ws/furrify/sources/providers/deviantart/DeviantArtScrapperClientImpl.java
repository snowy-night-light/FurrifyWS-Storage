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

    @Override
    public String scrapDeviationId(final String url) throws IOException {
        Document document = Jsoup.connect(url).get();
        Elements metaTags = document.getElementsByTag("meta");

        for (Element metaTag : metaTags) {
            String appUrl = metaTag.attr("property");

            if("da:appurl".equals(appUrl)) {
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
