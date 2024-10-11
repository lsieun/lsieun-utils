package lsieun.utils.asm.common;

import lsieun.utils.asm.match.AsmTypeMatch;
import lsieun.utils.asm.match.InsnInvokeMatch;
import lsieun.utils.asm.match.MethodInfoMatch;
import lsieun.utils.asm.match.result.MatchItem;
import lsieun.utils.core.ds.pair.Pair;
import lsieun.utils.core.io.resource.ResourceUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

class ClassFileFindUtilsTest {
    @Test
    void findMethodFromOneClassFileBySystemExit() throws IOException {
        byte[] bytes = ResourceUtils.readClassBytes(HelloWorldForSystemExit.class);
        List<MatchItem> itemList = ClassFileFindUtils.findMethodByInsnInvoke(
                bytes,
                MethodInfoMatch.Bool.TRUE,
                InsnInvokeMatch.ByMethodInsn.SYSTEM_EXIT);
        itemList.forEach(System.out::println);
        assertNotEquals(0, itemList.size());
    }

    @Test
    void findMethodFromDirectory() throws IOException {
        Path dirPath = Paths.get("...");
        MethodInfoMatch methodMatch = MethodInfoMatch.byReturnType(
                AsmTypeMatch.byType("...")
        );
        List<Pair<Path, List<MatchItem>>> list = ClassFileFindUtils.findInDir(
                dirPath, 1,
                bytes -> ClassFileFindUtils.findMethod(bytes, methodMatch)
        );
        ClassFileFindUtils.print(list);
    }

    class HelloWorldForSystemExit {
        public void aaa() {
            System.out.println("aaa");
        }

        public void bbb() {
            System.out.println("bbb");
            System.exit(0);
        }

        public void ccc() {
            System.out.println("ccc");
        }
    }
}