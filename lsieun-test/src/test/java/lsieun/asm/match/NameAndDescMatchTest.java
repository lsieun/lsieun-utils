package lsieun.asm.match;

import lsieun.base.reflect.member.FieldUtils;
import org.junit.jupiter.api.Test;

class NameAndDescMatchTest {
    @Test
    void test() throws Exception {
        Class<?> clazz = Class.forName("lsieun.asm.match.NameAndDescMatch.All");
        NameAndDescMatch instance = FieldUtils.<NameAndDescMatch>getFieldValue(clazz, "INSTANCE", null);
        boolean flag = instance.test("abc", "def");
        System.out.println(flag);
    }
}
