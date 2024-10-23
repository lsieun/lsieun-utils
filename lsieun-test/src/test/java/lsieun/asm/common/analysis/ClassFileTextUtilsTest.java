package lsieun.asm.common.analysis;

import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

class ClassFileTextUtilsTest {
    @Test
    void test() {
        Class<?> clazz = Object.class;
        Consumer<Class<?>> consumer = ClassFileTextUtils::printAsmCode;
        consumer.accept(clazz);
    }
}
