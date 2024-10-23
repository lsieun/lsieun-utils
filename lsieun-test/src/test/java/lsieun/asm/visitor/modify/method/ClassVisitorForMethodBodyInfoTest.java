package lsieun.asm.visitor.modify.method;

import lsieun.asm.common.transformation.ClassFileModifyUtils;
import lsieun.asm.sam.match.MethodInfoMatch;
import lsieun.asm.visitor.transformation.method.MethodBodyInfoType;
import lsieun.base.io.file.FileContentUtils;
import lsieun.base.io.resource.ResourceUtils;
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