package lsieun.utils.asm.common;

import lsieun.utils.asm.match.InsnInvokeMatch;
import lsieun.utils.asm.match.MethodInfoMatch;
import lsieun.utils.asm.match.result.MatchItem;
import lsieun.utils.core.io.resource.ResourceUtils;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

class ClassFileFindUtilsTest {
    @Test
    void findMethodFromOneClassFileBySystemExit() {
        byte[] bytes = ResourceUtils.readClassBytes(HelloWorldForSystemExit.class);
        List<MatchItem> itemList = ClassFileFindUtils.findMethodByInsnInvoke(
                bytes,
                MethodInfoMatch.Bool.TRUE,
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