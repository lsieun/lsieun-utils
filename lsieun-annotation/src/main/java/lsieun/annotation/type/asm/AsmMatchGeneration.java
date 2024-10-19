package lsieun.annotation.type.asm;

import lsieun.annotation.type.MarkerInterface;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@MarkerInterface
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface AsmMatchGeneration {
}
