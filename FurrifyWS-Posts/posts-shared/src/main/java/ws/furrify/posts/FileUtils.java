package ws.furrify.posts;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.detect.Detector;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Sky
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileUtils {

    public static String getMimeType(String filename, InputStream inputStream) throws IOException {
        TikaConfig config = TikaConfig.getDefaultConfig();
        Detector detector = config.getDetector();

        TikaInputStream stream = TikaInputStream.get(inputStream);

        Metadata metadata = new Metadata();
        metadata.add(Metadata.RESOURCE_NAME_KEY, filename);

        return detector.detect(stream, metadata).toString();
    }
}
