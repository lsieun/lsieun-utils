package lsieun.utils.core.reflect.clazz;

import lsieun.utils.annotation.mind.logic.ThinkStep;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class ClassFindUtils {
    public static List<Class<?>> getInnerClassList(Class<?> rootClazz, boolean includeItself) {
        Objects.requireNonNull(rootClazz);

        @ThinkStep("(1) root")
        List<Class<?>> clazzList = new ArrayList<>();
        clazzList.add(rootClazz);


        @ThinkStep("(2) inner classes")
        String rootTypeName = rootClazz.getTypeName();
        for (int i = 0; i < clazzList.size(); i++) {
            Class<?> clazz = clazzList.get(i);
            Class<?>[] innerClasses = clazz.getClasses();

            int length = innerClasses.length;

            for (int j = 0; j < length; j++) {
                Class<?> innerClazz = innerClasses[j];
                String typeName = innerClazz.getTypeName();

                // 必须当前类下面定义的类
                if (!typeName.contains(rootTypeName)) {
                    continue;
                }

                // 避免重复
                if (!clazzList.contains(innerClazz)) {
                    clazzList.add(innerClazz);
                }
            }
        }

        @ThinkStep("(3) sort")
        boolean needSort = !clazzList.isEmpty() && clazzList.size() > 1;
        if (needSort) {
            clazzList.sort(Comparator.comparing(Class::getTypeName));
        }
        if (!includeItself) {
            clazzList.remove(0);
        }

        return clazzList;
    }
}
