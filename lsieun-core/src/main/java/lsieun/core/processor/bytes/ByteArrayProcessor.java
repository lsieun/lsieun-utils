package lsieun.core.processor.bytes;

import org.jetbrains.annotations.NotNull;
import lsieun.base.coll.ListUtils;

import java.util.List;
import java.util.function.Function;

@FunctionalInterface
public interface ByteArrayProcessor extends Function<byte[], byte[]> {

    static ByteArrayProcessor of(ByteArrayProcessor... processors) {
        List<ByteArrayProcessor> list = ListUtils.toList(processors);
        return of(list);
    }

    static ByteArrayProcessor of(List<ByteArrayProcessor> list) {
        if (ListUtils.isNullOrEmpty(list)) {
            return NoOp.INSTANCE;
        }
        else if (list.size() == 1) {
            return list.get(0);
        }
        else {
            return new Compound(list);
        }
    }

    enum NoOp implements ByteArrayProcessor {
        INSTANCE;

        @Override
        public byte[] apply(byte[] bytes) {
            return bytes;
        }
    }

    class Compound implements ByteArrayProcessor {
        private final List<ByteArrayProcessor> processors;

        private Compound(@NotNull List<ByteArrayProcessor> processors) {
            this.processors = processors;
        }

        @Override
        public byte[] apply(byte[] bytes) {
            for (ByteArrayProcessor p : processors) {
                bytes = p.apply(bytes);
            }
            return bytes;
        }
    }
}
