package lsieun.utils.asm.match;

import lsieun.utils.core.io.file.FileContentUtils;
import lsieun.utils.match.text.TextMatch;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassReader;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class MatchAsmUtilsTest {
    @Test
    void testFindSingleAbstractMethod() {
        Class<?> clazz = TextMatch.class;
        Method samMethod = MatchAsmUtils.findSingleAbstractMethod(clazz);
        assertNotNull(samMethod);
        System.out.println(samMethod);
    }

    @Test
    void testGenerateAndSaveEnumTrue() throws IOException {
        Class<?> clazz = TextMatch.class;
        Function<Class<?>, byte[]> func = MatchAsmUtils::generateEnumTrue;
        generateAndSave(clazz, func);
    }

    @Test
    void testGenerateAndSaveEnumFalse() throws IOException {
        Class<?> clazz = TextMatch.class;
        Function<Class<?>, byte[]> func = MatchAsmUtils::generateEnumFalse;
        generateAndSave(clazz, func);
    }

    @Test
    void testGenerateAndSaveLogicAnd() throws IOException {
        Class<?> clazz = TextMatch.class;
        Function<Class<?>, byte[]> func = MatchAsmUtils::generateLogicAnd;
        generateAndSave(clazz, func);
    }

    private void generateAndSave(Class<?> clazz, Function<Class<?>, byte[]> func) throws IOException {
        byte[] bytes = func.apply(clazz);
        assertNotNull(bytes);

        ClassReader cr = new ClassReader(bytes);
        String className = cr.getClassName();
        String entryName = className + ".class";

        Path dir = Path.of(".", "target", "classes");
        Path filePath = dir.resolve(entryName).normalize();
        FileContentUtils.writeBytes(filePath, bytes);
    }


    @Test
    void testAndByReflection() throws ClassNotFoundException, InvocationTargetException, IllegalAccessException {
        Class<?> clazz = Class.forName("lsieun.utils.match.text.TextMatchAnd");
        System.out.println(clazz);
        Method[] declaredMethods = clazz.getDeclaredMethods();
        Method targetMethod = null;
        for (Method declaredMethod : declaredMethods) {
            String name = declaredMethod.getName();
            if (name.equals("of")) {
                targetMethod = declaredMethod;
            }
            System.out.println(declaredMethod);
        }
//        Method method = clazz.getDeclaredMethod("of");
//        System.out.println(method);

        TextMatch[] array = {
                TextMatch.startsWith("You"),
                TextMatch.endsWith("fly")
        };
        assert targetMethod != null;
        Object obj = targetMethod.invoke(null, (Object[]) array);
        System.out.println(obj);
    }

    @Test
    void testAndByMethodInvoke() {
//        String str = "You don't need wings to fly";
//        TextMatch[] array = {
//                TextMatch.startsWith("You"),
//                TextMatch.endsWith("fly")
//        };
//        TextMatch textMatch = MatchAsmUtils.and(TextMatch.class, array);
//        boolean flag = textMatch.test(str);
//        System.out.println(flag);
    }


}