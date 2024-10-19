package lsieun.asm.common.generation;

import lsieun.asm.match.NameAndDescMatch;
import lsieun.base.reflect.member.FieldUtils;
import lsieun.base.reflect.member.MemberFindUtils;
import lsieun.base.reflect.member.MethodInvokeUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.util.List;

public class ClassFileGenerationUtilsTest {
    @Test
    void testGenerateClass() throws IOException {
        ClassFileGenerationUtils.generateMatch(NameAndDescMatch.class);
    }

    @Test
    void testGenerateClassTrueAndFalse() throws Exception {
        Class<?> clazz = NameAndDescMatch.class;
        List<Method> methodList = MemberFindUtils.getMethodList(clazz);
        List<Method> candidateList = methodList.stream()
                .filter(m -> {
                    int modifiers = m.getModifiers();
                    if (Modifier.isStatic(modifiers)) {
                        return false;
                    }
                    return Modifier.isPublic(modifiers);
                }).toList();
        String[] innerNames = {"All", "None"};
        for (String innerName : innerNames) {
            String className = String.format("%s$%s", clazz.getName(), innerName);
            Class<?> newClazz = Class.forName(className);
            NameAndDescMatch instance = FieldUtils.<NameAndDescMatch>getFieldValue(newClazz, "INSTANCE", null);
            MethodInvokeUtils.invokeMethodListWithObj(candidateList, List.of(instance));
        }

    }

    @Test
    void testGenerateDir() throws IOException {
        Path dirPath = Path.of("D:/git-repo/lsieun-utils/");
        MultipleClassFileGenerationBuilder.generateMatch(dirPath, 20);
    }
}
