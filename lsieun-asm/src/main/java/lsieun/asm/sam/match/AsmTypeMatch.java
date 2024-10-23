package lsieun.asm.sam.match;

import lsieun.annotation.type.asm.AsmMatchGeneration;
import lsieun.asm.core.AsmTypeNameUtils;
import lsieun.core.match.LogicAssistant;
import lsieun.core.match.text.TextMatch;

import org.objectweb.asm.Type;

import java.lang.invoke.MethodHandles;
import java.util.Objects;
import java.util.function.Function;

@AsmMatchGeneration
@FunctionalInterface
public interface AsmTypeMatch {
    boolean test(Type type);

    static AsmTypeMatch byType(String text) {
        Type t = AsmTypeNameUtils.parse(text);
        return byType(t);
    }

    static AsmTypeMatch byType(Class<?> clazz) {
        Type t = Type.getType(clazz);
        return byType(t);
    }

    static AsmTypeMatch byType(Type t) {
        return s -> Objects.equals(s, t);
    }

    static AsmTypeMatch bySimpleName(TextMatch textMatch) {
        return byName(AsmTypeNameUtils::toSimpleName, textMatch);
    }

    static AsmTypeMatch byClassName(TextMatch textMatch) {
        return byName(AsmTypeNameUtils::toClassName, textMatch);
    }

    static AsmTypeMatch byInternalName(TextMatch textMatch) {
        return byName(AsmTypeNameUtils::toInternalName, textMatch);
    }

    static AsmTypeMatch byDescriptor(TextMatch textMatch) {
        return byName(AsmTypeNameUtils::toDescriptor, textMatch);
    }

    static AsmTypeMatch byJarEntryName(TextMatch textMatch) {
        return byName(AsmTypeNameUtils::toJarEntryName, textMatch);
    }

    static AsmTypeMatch byName(Function<Type, String> func, TextMatch textMatch) {
        return t -> textMatch.test(func.apply(t));
    }

    // TODO: 所有的 match 应该有 logic 方法
    static LogicAssistant<AsmTypeMatch> logic() {
        return LogicAssistant.of(MethodHandles.lookup(), AsmTypeMatch.class);
    }

    enum Bool implements AsmTypeMatch {
        TRUE {
            @Override
            public boolean test(Type type) {
                return true;
            }
        },
        FALSE {
            @Override
            public boolean test(Type type) {
                return false;
            }
        };
    }
}
