package lsieun.utils.core.io.resource;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public enum ResourceStrategy implements ResourceParser {
    NoOp {
        @Override
        public Optional<Path> parse(String filepath) {
            return Optional.empty();
        }
    },
    ABSOLUTE_PATH {
        @Override
        public Optional<Path> parse(String filepath) {
            Path path = Paths.get(filepath);
            if (Files.exists(path)) {
                path = path.toAbsolutePath().normalize();
                return Optional.of(path);
            }
            return Optional.empty();
        }
    },
    RELATIVE_PATH {
        @Override
        public Optional<Path> parse(String filepath) {
            Path path = Paths.get(".", filepath);
            if (Files.exists(path)) {
                path = path.toAbsolutePath().normalize();
                return Optional.of(path);
            }
            return Optional.empty();
        }
    },
    CLASSLOADER {
        @Override
        public Optional<Path> parse(String filepath) {
            URL resource = ResourceUtils.class.getClassLoader().getResource(filepath);
            if (resource == null) {
                return Optional.empty();
            }

            try {
                URI uri = resource.toURI();
                Path path = Paths.get(uri);
                path = path.toAbsolutePath().normalize();
                return Optional.of(path);
            } catch (URISyntaxException e) {
                return Optional.empty();
            }
        }
    },
    ;

}
