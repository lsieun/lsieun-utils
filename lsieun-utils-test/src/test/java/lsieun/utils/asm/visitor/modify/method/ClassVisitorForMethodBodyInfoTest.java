package lsieun.utils.asm.visitor.modify.method;

import lsieun.utils.asm.match.MethodInfoMatch;
import lsieun.utils.asm.common.ClassFileModifyUtils;
import lsieun.utils.core.io.file.FileContentUtils;
import lsieun.utils.core.io.resource.ResourceUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.EnumSet;

class ClassVisitorForMethodBodyInfoTest {
    @Test
    void testPrintMethodParameter() throws IOException {
        Class<?> clazz = HelloWorldForPrintMethodParameter.class;
        Path path = ResourceUtils.readFilePath(clazz);
        byte[] bytes = FileContentUtils.readBytes(path);

        byte[] newBytes = ClassFileModifyUtils.printMethodInfo(
                bytes,
                MethodInfoMatch.byMethodName("test"),
                EnumSet.of(
                        MethodBodyInfoType.ENTER,
                        MethodBodyInfoType.PARAMETER_VALUES,
                        MethodBodyInfoType.EXIT
                )
        );
        FileContentUtils.writeBytes(path, newBytes);
    }

    static class HelloWorldForPrintMethodParameter {
        public static void test(String name, int age, Object obj) {
        }

        public static void main(String[] args) {
            test("Tom", 10, LocalDate.now());
        }
    }
}