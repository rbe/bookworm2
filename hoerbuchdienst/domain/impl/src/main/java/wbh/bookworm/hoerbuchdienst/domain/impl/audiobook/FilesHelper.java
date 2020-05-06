package wbh.bookworm.hoerbuchdienst.domain.impl.audiobook;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class FilesHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(FilesHelper.class);

    private FilesHelper() {
        throw new AssertionError();
    }

    static void tryDelete(final Path path) {
        try {
            Files.delete(path);
        } catch (IOException e) {
            LOGGER.error("", e);
        }
    }

}
