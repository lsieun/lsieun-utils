package lsieun.utils.asm.common;

import lsieun.utils.asm.match.ClassInfoMatch;
import lsieun.utils.asm.match.NameAndDescMatch;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

class ClassFileTextUtilsTest {
    @Test
    void test() throws ClassNotFoundException {
        Class<?> clazz = ClassInfoMatch.class;
        Consumer<Class<?>> consumer = ClassFileTextUtils::printAsmCode;
        consumer.accept(clazz);

        Class<?> cls = Class.forName("lsieun.utils.asm.match.NameAndDescMatch$All");
        System.out.println(cls);

//                Class<?> clazz = Class.forName("lsieun.utils.asm.match.NameAndDescMatch.All");
//        NameAndDescMatch instance = FieldUtils.<NameAndDescMatch>getFieldValue(clazz, "INSTANCE", null);
//        boolean flag = instance.test("abc", "def");
//        System.out.println(flag);
    }
}
