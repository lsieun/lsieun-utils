package lsieun.utils.asm.description;

import jakarta.annotation.Nonnull;

import java.util.Arrays;
import java.util.function.Predicate;

public record MemberDesc(String owner, String name, String desc) {
    public static MemberDesc of(String owner, String name, String desc) {
        return new MemberDesc(owner, name, desc);
    }

    public static MemberDesc of(@Nonnull String line) {
        return of(line, ":");
    }

    public static MemberDesc of(@Nonnull String line, String regex) {
        String[] array = line.trim().split(regex);
        String[] parts = Arrays.stream(array).
                map(String::trim)
                .filter(Predicate.not(String::isBlank))
                .toArray(String[]::new);
        return new MemberDesc(parts[0], parts[1], parts[2]);
    }
}
