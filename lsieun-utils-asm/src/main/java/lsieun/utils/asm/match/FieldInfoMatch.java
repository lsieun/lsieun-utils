package lsieun.utils.asm.match;

import lsieun.utils.match.text.TextMatch;

import java.lang.invoke.MethodHandles;
import java.util.Objects;

public interface FieldInfoMatch {
    boolean test(String owner, int fieldAccess, String fieldName, String fieldDesc, Object value);

    static FieldInfoMatch byFieldName(String name) {
        return byFieldName(TextMatch.equals(name));
    }

    static FieldInfoMatch byFieldName(TextMatch textMatch) {
        return byNameAndDesc(textMatch, TextMatch.Bool.TRUE);
    }

    static FieldInfoMatch byNameAndDesc(String name, String desc) {
        return byNameAndDesc(TextMatch.equals(name), TextMatch.equals(desc));
    }

    static FieldInfoMatch byNameAndDesc(TextMatch nameMatch, TextMatch descMatch) {
        return ((owner, fieldAccess, fieldName, fieldDesc, value) ->
                nameMatch.test(fieldName) && descMatch.test(fieldDesc));
    }

    static FieldInfoMatch byValue(Object val) {
        return ((owner, fieldAccess, fieldName, fieldDesc, value) -> Objects.equals(val, value));
    }

    static LogicAssistant<FieldInfoMatch> logic() {
        return LogicAssistant.of(MethodHandles.lookup(), FieldInfoMatch.class);
    }

    enum Bool implements FieldInfoMatch {
        TRUE {
            @Override
            public boolean test(String owner, int fieldAccess, String fieldName, String fieldDesc, Object value) {
                return true;
            }
        },
        FALSE {
            @Override
            public boolean test(String owner, int fieldAccess, String fieldName, String fieldDesc, Object value) {
                return false;
            }
        };
    }
}
