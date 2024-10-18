package lsieun.utils.asm.common;

import lsieun.utils.asm.common.analysis.ClassFileFindBuilder;
import lsieun.utils.asm.common.analysis.ClassFileFindUtils;
import lsieun.utils.asm.common.analysis.MultipleClassFileFindUtils;
import lsieun.utils.asm.match.AsmTypeMatch;
import lsieun.utils.asm.match.InsnInvokeMatch;
import lsieun.utils.asm.match.LogicAssistant;
import lsieun.utils.asm.match.MethodInfoMatch;
import lsieun.utils.asm.match.result.MatchItem;
import lsieun.utils.core.ds.pair.Pair;
import lsieun.utils.match.text.TextMatch;
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
        LogicAssistant<TextMatch> logic = LogicAssistant.of(TextMatch.lookup(), TextMatch.class);
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
        MethodHandles.Lookup lookup = TextMatch.lookup();

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
