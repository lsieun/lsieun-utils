package lsieun.asm.common.analysis;

import lsieun.asm.builder.ClassFileFindBuilder;
import lsieun.asm.core.AsmTypeNameUtils;
import lsieun.asm.description.MemberDesc;
import lsieun.asm.sam.match.FieldInfoMatch;
import lsieun.asm.sam.match.MethodInfoMatch;
import lsieun.core.sam.match.text.TextMatch;
import lsieun.core.sam.match.text.TextMatchBuddy;

import java.nio.file.Path;

public class ClassFileFindUtilsRun {
    public static void main(String[] args) {
        findField();
        findMethod();
        findMethodByStackFrameLine();
    }

    public static void findField() {
        // (1) dir
        Path dirPath = Path.of("...");

        // (2) match: entry and field
        TextMatch zipEntryMatch = TextMatchBuddy.ignoreThirdParty();
        FieldInfoMatch fieldMatch = FieldInfoMatch.byValue(7);

        // (3) find
        ClassFileFindBuilder.byField()
                .withFromDir(dirPath, 1, false)
                .withEntryName(zipEntryMatch)
                .withClassMatch()
                .withFieldMatch(fieldMatch)
                .run();
    }

    public static void findMethod() {
        // (1) dir
        Path dirPath = Path.of("...");

        // (2) match: entry and method
        String entry = "com/abc/Xyz.class";
        String methodName = "test";
        String methodDesc = "()V";

        TextMatch zipEntryMatch = TextMatch.contains(entry);
        MethodInfoMatch methodMatch = MethodInfoMatch.byMethodNameAndDesc(methodName, methodDesc);

        // (3) find
        ClassFileFindBuilder.byMethod()
                .withFromDir(dirPath, 1, false)
                .withEntryName(zipEntryMatch)
                .withClassMatch()
                .withMethodMatch(methodMatch)
                .run();
    }

    public static void findMethodByStackFrameLine() {
        // (1) dir
        Path dirPath = Path.of("...");

        // (2) match: entry and method
        String stackFrameLine = "...";
        MemberDesc memberDesc = MemberDesc.of(stackFrameLine);

        TextMatch zipEntryMatch = TextMatch.contains(
                AsmTypeNameUtils.toInternalName(memberDesc.owner())
        );
        MethodInfoMatch methodMatch = MethodInfoMatch.byMethodNameAndDesc(
                memberDesc.name(),
                memberDesc.desc()
        );

        // (3) find
        ClassFileFindBuilder.byMethod()
                .withFromDir(dirPath, 1, false)
                .withEntryName(zipEntryMatch)
                .withClassMatch()
                .withMethodMatch(methodMatch)
                .run();
    }
}
