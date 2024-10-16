package lsieun.utils.asm.description;

import jakarta.annotation.Nonnull;
import lsieun.utils.asm.core.AsmTypeNameUtils;
import lsieun.utils.core.text.StrConst;
import lsieun.utils.core.text.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public record MemberDesc(String owner, String name, String desc) {
    public static MemberDesc of(String owner, String name, String desc) {
        return new MemberDesc(owner, name, desc);
    }

    public static MemberDesc of(@Nonnull String line) {
        return of(line, StrConst.COLON);
    }

    public static MemberDesc of(@Nonnull String line, @Nonnull String regex) {
        String[] array = line.trim().split(regex);
        String[] parts = Arrays.stream(array).
                map(String::trim)
                .filter(Predicate.not(String::isBlank))
                .toArray(String[]::new);
        return new MemberDesc(AsmTypeNameUtils.toInternalName(parts[0]), parts[1], parts[2]);
    }

    public static List<MemberDesc> ofList(@Nonnull List<String> lines) {
        return ofList(lines, StrConst.COLON);
    }

    public static List<MemberDesc> ofList(@Nonnull List<String> lines, @Nonnull String regex) {
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
