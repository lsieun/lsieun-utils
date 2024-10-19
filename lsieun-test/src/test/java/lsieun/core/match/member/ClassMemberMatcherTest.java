package lsieun.core.match.member;

import lsieun.base.reflect.member.MemberFindUtils;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.List;

import static lsieun.core.match.member.ClassMemberMatcher.*;
import static lsieun.core.match.modifier.ModifierMatcher.isPublic;
import static lsieun.core.match.modifier.ModifierMatcher.isStatic;
import static lsieun.core.match.modifier.ModifierMatcher.isSynthetic;
import static lsieun.core.match.name.NameMatcher.named;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ClassMemberMatcherTest {
    @Test
    void testNamed() {
        Class<?> clazz = URI.class;
        String targetMethodName = "decode";

        List<Method> methodList = MemberFindUtils.getMethodList(clazz);

        methodList = MemberFindUtils.filter(methodList, named(Member::getName, targetMethodName));

        for (Method m : methodList) {
            System.out.println(m);
            String name = m.getName();
            assertEquals(name, targetMethodName);
        }
    }

    @Test
    void testModifier() {
        Class<?> clazz = URI.class;
        List<Method> methodList = MemberFindUtils.getMethodList(clazz);

        methodList = MemberFindUtils.filter(
                methodList,
                isPublic(Member::getModifiers).and(isStatic(Member::getModifiers))
        );

        for (Method m : methodList) {
            System.out.println(m);
        }
    }

    @Test
    void testParamCount() {
        Class<?> clazz = URI.class;
        List<Method> methodList = MemberFindUtils.getMethodList(clazz);

        methodList = MemberFindUtils.filter(
                methodList,
                paramCount(0).and(isSynthetic(Member::getModifiers).negate()).and(isPublic(Member::getModifiers))
        );

        for (Method m : methodList) {
            System.out.println(m);
        }
    }

    @Test
    void testParamTypeContains() {
        Class<?> clazz = URI.class;
        List<Method> methodList = MemberFindUtils.getMethodList(clazz);

        methodList = MemberFindUtils.filter(
                methodList,
                paramTypeContains(String.class)
        );

        for (Method m : methodList) {
            System.out.println(m);
        }
    }

    @Test
    void testParamTypeEquals() {
        Class<?> clazz = URI.class;
        List<Method> methodList = MemberFindUtils.getMethodList(clazz);

        methodList = MemberFindUtils.filter(
                methodList,
                paramTypeEquals(int.class, String.class)
        );

        for (Method m : methodList) {
            System.out.println(m);
        }
    }

    @Test
    void testReturnTypeEquals() {
        Class<?> clazz = URI.class;
        List<Method> methodList = MemberFindUtils.getMethodList(clazz);

        methodList = MemberFindUtils.filter(
                methodList,
                returnTypeEquals(boolean.class)
        );

        for (Method m : methodList) {
            System.out.println(m);
        }
    }

    @Test
    void testReturnTypeWithin() {
        Class<?> clazz = URI.class;
        List<Method> methodList = MemberFindUtils.getMethodList(clazz);

        methodList = MemberFindUtils.filter(
                methodList,
                returnTypeWithin(boolean.class, int.class)
        );

        for (Method m : methodList) {
            System.out.println(m);
        }
    }
}