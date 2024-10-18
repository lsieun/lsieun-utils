package lsieun.utils.asm.common;

import lsieun.utils.asm.common.analysis.ClassFileTextUtils;
import org.junit.jupiter.api.Test;
import sample.HelloWorld;

import java.util.function.Consumer;

class ClassFileTextUtilsTest {
    @Test
    void test() {
        Class<?> clazz = HelloWorld.class;
        Consumer<Class<?>> consumer = ClassFileTextUtils::printAsmCode;
        consumer.accept(clazz);
    }
}
