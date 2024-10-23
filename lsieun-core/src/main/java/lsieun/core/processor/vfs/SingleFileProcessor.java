package lsieun.core.processor.vfs;

import lsieun.core.processor.bytes.ByteArrayProcessor;
import lsieun.core.processor.bytes.ByteArrayProcessorBuilder;

import java.nio.file.Path;
import java.util.function.Consumer;

@FunctionalInterface
public interface SingleFileProcessor extends Consumer<Path> {

    static SingleFileProcessor byByteArrayProcessor(ByteArrayProcessor... processors) {
        return filepath -> {
            ByteArrayProcessor p = ByteArrayProcessor.of(processors);
            ByteArrayProcessorBuilder.forFile()
                    .withFile(filepath)
                    .withByteArrayProcessor(p)
                    .run();
        };
    }
}
