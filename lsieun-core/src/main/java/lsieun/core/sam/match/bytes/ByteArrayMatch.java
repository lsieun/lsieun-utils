package lsieun.core.sam.match.bytes;

import lsieun.core.match.LogicAssistant;

import java.lang.invoke.MethodHandles;

@FunctionalInterface
public interface ByteArrayMatch {
    boolean test(byte[] bytes);

    LogicAssistant<ByteArrayMatch> LOGIC = LogicAssistant.of(MethodHandles.lookup(), ByteArrayMatch.class);
}
