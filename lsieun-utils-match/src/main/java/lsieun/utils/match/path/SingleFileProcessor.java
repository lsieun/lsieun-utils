package lsieun.utils.match.path;

import lsieun.utils.core.bytes.ByteArrayProcessor;
import lsieun.utils.core.bytes.ByteArrayProcessorBuilder;

import java.nio.file.Path;
import java.util.function.Consumer;

@FunctionalInterface
public interface SingleFileProcessor extends Consumer<Path> {

    static SingleFileProcessor byByteArrayProcessor(ByteArrayProcessor... processors) {
        return filepath -> {
            ByteArrayProcessor p = ByteArrayProcessor.of(processors);
            ByteArrayProcessorBuilder.forFile()
                    .withFile(filepath)
                    .withFunction(p);
        };
    }
}
