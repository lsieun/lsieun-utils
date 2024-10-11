package lsieun.utils.annotation.type;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@MarkerInterface
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE})
public @interface UtilityClass {
}