package ru.avtomir.googlesheets.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Util for resolving path to streams against user.dir.
 */
public class PathUtil {
    private final static Logger logger = LoggerFactory.getLogger(PathUtil.class);

    public static Path resolveToPath(String path) {
        String userDir = System.getProperty("user.dir");
        logger.trace("resolve path {} to `Path` against dir: {}", path, userDir);
        return Paths.get(userDir, path);
    }

    public static InputStreamReader resolveToStream(String path) {
        try {
            String userDir = System.getProperty("user.dir");
            logger.trace("resolve path {} to `Stream` against dir: {}", path, userDir);
            return new InputStreamReader(Files.newInputStream(Paths.get(userDir, path)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
