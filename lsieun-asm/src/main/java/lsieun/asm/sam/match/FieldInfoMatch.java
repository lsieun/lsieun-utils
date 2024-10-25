package lsieun.asm.sam.match;

import lsieun.core.match.LogicAssistant;
import lsieun.core.sam.match.text.TextMatch;

import java.lang.invoke.MethodHandles;
import java.util.Objects;

public interface FieldInfoMatch {
    boolean test(String owner, int fieldAccess, String fieldName, String fieldDesc, Object value);

    LogicAssistant<FieldInfoMatch> LOGIC = LogicAssistant.of(MethodHandles.lookup(), FieldInfoMatch.class);

    static FieldInfoMatch byFieldName(String name) {
        return byFieldName(TextMatch.equals(name));
    }

    static FieldInfoMatch byFieldName(TextMatch textMatch) {
        return byNameAndDesc(textMatch, TextMatch.LOGIC.alwaysTrue());
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
}
