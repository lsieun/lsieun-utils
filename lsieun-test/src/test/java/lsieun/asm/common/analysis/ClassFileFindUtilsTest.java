package lsieun.asm.common.analysis;

import lsieun.asm.sam.match.InsnInvokeMatch;
import lsieun.asm.sam.match.MethodInfoMatch;
import lsieun.asm.match.MatchItem;
import lsieun.base.io.resource.ResourceUtils;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

class ClassFileFindUtilsTest {
    @Test
    void findMethodFromOneClassFileBySystemExit() {
        byte[] bytes = ResourceUtils.readClassBytes(HelloWorldForSystemExit.class);
        List<MatchItem> itemList = ClassFileFindUtils.findMethodByInsnInvoke(
                bytes,
                MethodInfoMatch.LOGIC.alwaysTrue(),
                InsnInvokeMatch.ByMethodInsn.SYSTEM_EXIT);
        itemList.forEach(System.out::println);
        assertNotEquals(0, itemList.size());
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