package lsieun.base.io.resource;

import lsieun.base.io.IOUtils;
import lsieun.base.io.file.FileFormatUtils;
import lsieun.base.io.file.FileOperation;
import lsieun.base.log.Logger;
import lsieun.base.log.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Optional;

public class ResourceUtils {
    private static final Logger logger = LoggerFactory.getLogger(ResourceUtils.class);

    public static Path readFilePath(Class<?> clazz) {
        String typeName = clazz.getTypeName();
        String relativePath = typeName.replace('.', '/') + ".class";
        return readFilePath(relativePath);
    }

    public static Path readFilePath(String filepath) {
        ResourceStrategy[] values = ResourceStrategy.values();
        int length = values.length;
        for (int i = 0; i < length; i++) {
            ResourceStrategy strategy = values[i];
            Optional<Path> op = strategy.parse(filepath);
            if (op.isPresent()) {
                Path path = op.get();
                logger.debug(() -> FileFormatUtils.format(path, FileOperation.FIND));
                return path;
            }
        }

        String msg = String.format("filepath NOT FOUND: %s", filepath);
        throw new IllegalArgumentException(msg);
    }

    public static byte[] readClassBytes(Class<?> clazz) {
        String typeName = clazz.getTypeName();
        String resourcePath = typeName.replace('.', '/') + ".class";
        try (InputStream in = ClassLoader.getSystemResourceAsStream(resourcePath)) {
            try {
                URL resource = ClassLoader.getSystemResource(resourcePath);
                URI uri = resource.toURI();
                String scheme = uri.getScheme();
                if (scheme.equals("file")) {
                    Path path = Path.of(uri);
                    logger.info(() -> String.format("[READ RESOURCE] %s", path.toUri()));
                }
                else {
                    logger.info(() -> String.format("[READ RESOURCE] %s", uri));
                }
            } catch (URISyntaxException ignored) {
            }

            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            IOUtils.copy(in, bao);
            return bao.toByteArray();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
