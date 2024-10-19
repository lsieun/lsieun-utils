package lsieun.asm.common.analysis;

import lsieun.asm.sample.ToString;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

class ClassFileTextUtilsTest {
    @Test
    void test() {
        Class<?> clazz = ToString.class;
        Consumer<Class<?>> consumer = ClassFileTextUtils::printAsmCode;
        consumer.accept(clazz);
    }
}
