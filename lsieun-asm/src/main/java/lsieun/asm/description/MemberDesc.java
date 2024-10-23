package lsieun.asm.description;

import lsieun.asm.core.AsmTypeNameUtils;
import lsieun.base.text.StrConst;
import lsieun.base.text.StringUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public record MemberDesc(String owner, String name, String desc) {
    public static MemberDesc of(String owner, String name, String desc) {
        return new MemberDesc(owner, name, desc);
    }

    public static MemberDesc of(@NotNull String line) {
        return of(line, StrConst.COLON);
    }

    public static MemberDesc of(@NotNull String line, @NotNull String regex) {
        String[] array = line.trim().split(regex);
        String[] parts = Arrays.stream(array).
                map(String::trim)
                .filter(Predicate.not(String::isBlank))
                .toArray(String[]::new);
        return new MemberDesc(AsmTypeNameUtils.toInternalName(parts[0]), parts[1], parts[2]);
    }

    public static List<MemberDesc> ofList(@NotNull List<String> lines) {
        return ofList(lines, StrConst.COLON);
    }

    public static List<MemberDesc> ofList(@NotNull List<String> lines, @NotNull String regex) {
        if (lines.isEmpty()) {
            return Collections.emptyList();
        }

        List<MemberDesc> resultList = new ArrayList<>();
        for (String str : lines) {
            if (StringUtils.isBlank(str)) {
                continue;
            }
            str = str.trim();
            if (str.startsWith("#")) {
                continue;
            }
            MemberDesc desc = MemberDesc.of(str, regex);
            if (!resultList.contains(desc)) {
                resultList.add(desc);
            }
        }
        return resultList;
    }
}
