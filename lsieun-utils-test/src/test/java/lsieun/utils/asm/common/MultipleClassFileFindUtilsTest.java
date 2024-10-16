package lsieun.utils.asm.common;

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
        TextMatch textMatch = TextMatch.contains("match");
        ClassFileFindBuilder.byEntryName()
                .withDir(dirPath, 10, false)
                .withTextMatch(textMatch)
                .print();
    }

    @Test
    void findClassFromDir2() {
        Path dirPath = Path.of("D:\\tmp\\intellij\\lib");
        MethodHandles.Lookup lookup = TextMatch.lookup();
        LogicAssistant<TextMatch> logic = LogicAssistant.<TextMatch>builder()
                .withClass(TextMatch.class)
                .withLookup(lookup);
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
                .withMethodMatch(methodMatch)
                .print();
    }



    @Test
    void findInsnromDir2() {
        Path dirPath = Path.of("D:\\tmp\\intellij\\lib");
        MethodHandles.Lookup lookup = TextMatch.lookup();

        InsnInvokeMatch insnInvokeMatch = InsnInvokeMatch.by("doFinal");
        ClassFileFindBuilder.byInsn()
                .withDir(dirPath, 1, false)
                .withMethodMatch(MethodInfoMatch.Bool.TRUE)
                .withInsnMatch(insnInvokeMatch, false)
                .print();
    }
}
