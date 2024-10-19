package lsieun.asm.common;

import lsieun.asm.common.analysis.ClassFileFindBuilder;
import lsieun.asm.common.analysis.ClassFileFindUtils;
import lsieun.asm.common.analysis.MultipleClassFileFindUtils;
import lsieun.asm.match.AsmTypeMatch;
import lsieun.asm.match.InsnInvokeMatch;
import lsieun.core.match.LogicAssistant;
import lsieun.asm.match.MethodInfoMatch;
import lsieun.asm.match.result.MatchItem;
import lsieun.base.ds.pair.Pair;
import lsieun.core.match.text.TextMatch;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

class MultipleClassFileFindUtilsTest {
    @Test
    void findByByteArrayMethodFromDirectory() throws IOException {
        Path dirPath = Paths.get("...");
        MethodInfoMatch methodMatch = MethodInfoMatch.byReturnType(
                AsmTypeMatch.byType("...")
        );

        List<Pair<Path, List<MatchItem>>> list = MultipleClassFileFindUtils.findInDir(
                dirPath, 1,
                bytes -> ClassFileFindUtils.findMethod(bytes, methodMatch)
        );
        MultipleClassFileFindUtils.print(list);
    }

    @Test
    void findClassFromDir() {
        Path dirPath = Path.of("D:\\tmp\\intellij\\lib");
        TextMatch textMatch = TextMatch.containsIgnoreCase("ascii");
        ClassFileFindBuilder.byEntryName()
                .withDir(dirPath, 10, false)
                .withTextMatch(textMatch)
                .print();
    }

    @Test
    void findClassFromDir2() {
        Path dirPath = Path.of("D:\\tmp\\intellij\\lib");
        LogicAssistant<TextMatch> logic = TextMatch.logic();
        TextMatch textMatch = logic.and(
                TextMatch.contains("/PsiElement.class")
//                TextMatch.contains("Analy"),
//                logic.not(TextMatch.contains("asm"))
        );
        ClassFileFindBuilder.byEntryName()
                .withDir(dirPath, 1, false)
                .withTextMatch(textMatch)
                .print();
    }

    @Test
    void findMethodFromDir() {
        Path dirPath = Path.of("D:\\tmp\\intellij\\lib");
        MethodInfoMatch methodMatch = MethodInfoMatch.byMethodName(
                TextMatch.startsWith("draw")
        );
        ClassFileFindBuilder.byMethod()
                .withDir(dirPath, 1, false)
                .withZipEntryMatch()
                .withClassInfoMatch()
                .withMethodMatch(methodMatch)
                .print();
    }



    @Test
    void findInsnFromDir2() {
        Path dirPath = Path.of("D:\\tmp\\intellij\\lib");

        InsnInvokeMatch insnInvokeMatch = InsnInvokeMatch.byMethodName("doFinal");
        ClassFileFindBuilder.byInsn()
                .withDir(dirPath, 1, false)
                .withZipEntryMatch()
                .withClassInfoMatch()
                .withMethodMatch(MethodInfoMatch.Bool.TRUE)
                .withInsnMatch(insnInvokeMatch, false)
                .print();
    }
}
