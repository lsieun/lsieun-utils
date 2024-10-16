package lsieun.utils.core.reflect.member;

import java.lang.reflect.Field;

public class FieldUtils {
    public static <T> T getFieldValue(Class<?> clazz, String fieldName, Object obj) throws Exception {
        Field f = clazz.getDeclaredField(fieldName);
        Object val = f.get(obj);
        return val == null ? null : (T) val;
    }
}
