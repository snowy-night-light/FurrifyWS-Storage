package ws.furrify.shared.util;

import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.nio.file.Files;

/**
 * Utility class regarding handling filesystem.
 */
@Log4j2
public class FileUtils {


    /**
     * Delete filesystem directory and its files.
     *
     * @param directory Directory to delete.
     * @throws IllegalStateException Could not delete directory.
     */
    public static void deleteDirectoryWithFiles(File directory) throws IllegalStateException {
        File[] contents = directory.listFiles();
        if (contents != null) {
            for (File file : contents) {
                if (!Files.isSymbolicLink(file.toPath())) {
                    deleteDirectoryWithFiles(file);
                }
            }
        }
        boolean result = directory.delete();

        if (!result) {
            log.error("Cannot delete directory [path=" + directory.getAbsolutePath() + "].");

            throw new IllegalStateException("Couldn't remove directory.");
        }
    }


}
