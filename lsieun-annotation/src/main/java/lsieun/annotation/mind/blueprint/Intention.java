package lsieun.annotation.mind.blueprint;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

@Retention(RetentionPolicy.CLASS)
@Target({TYPE, FIELD, CONSTRUCTOR, METHOD})
public @interface Intention {
    String[] value();
}
