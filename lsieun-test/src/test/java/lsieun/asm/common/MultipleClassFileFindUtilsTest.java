package lsieun.asm.common;

import lsieun.asm.builder.ClassFileFindBuilder;
import lsieun.asm.common.analysis.ClassFileFindUtils;
import lsieun.asm.common.analysis.MultipleClassFileFindUtils;
import lsieun.asm.sam.match.AsmTypeMatch;
import lsieun.asm.sam.match.InsnInvokeMatch;
import lsieun.asm.sam.match.MethodInfoMatch;
import lsieun.asm.match.MatchItem;
import lsieun.base.ds.box.pair.Pair;
import lsieun.core.match.LogicAssistant;
import lsieun.core.sam.match.text.TextMatch;

import org.junit.jupiter.api.Test;

import java.io.IOException;
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
                .withFromDir(dirPath, 10, false)
                .withEntryName(textMatch)
                .run();
    }

    @Test
    void findClassFromDir2() {
        Path dirPath = Path.of("D:\\tmp\\intellij\\lib");
        LogicAssistant<TextMatch> logic = TextMatch.LOGIC;
        TextMatch textMatch = logic.and(
                TextMatch.contains("/PsiElement.class")
//                TextMatch.contains("Analy"),
//                logic.not(TextMatch.contains("asm"))
        );
        ClassFileFindBuilder.byEntryName()
                .withFromDir(dirPath, 1, false)
                .withEntryName(textMatch)
                .run();
    }

    @Test
    void findMethodFromDir() {
        Path dirPath = Path.of("D:\\tmp\\intellij\\lib");
        MethodInfoMatch methodMatch = MethodInfoMatch.byMethodName(
                TextMatch.startsWith("draw")
        );
        ClassFileFindBuilder.byMethod()
                .withFromDir(dirPath, 1, false)
                .withEntryName()
                .withClassMatch()
                .withMethodMatch(methodMatch)
                .run();
    }


    @Test
    void findInsnFromDir2() {
        Path dirPath = Path.of("D:\\tmp\\intellij\\lib");

        InsnInvokeMatch insnInvokeMatch = InsnInvokeMatch.byMethodName("doFinal");
        ClassFileFindBuilder.byInsn()
                .withFromDir(dirPath, 1, false)
                .withEntryName()
                .withClassMatch()
                .withMethodMatch(MethodInfoMatch.LOGIC.alwaysTrue())
                .withInsnMatch(insnInvokeMatch, false)
                .run();
    }
}
