package lsieun.base.io.resource;

import org.junit.jupiter.api.Test;

import javax.swing.tree.TreeModel;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ResourceUtilsTest {

    @Test
    void readFilePath() {
    }

    @Test
    void readClassBytes() {
        byte[] bytes = ResourceUtils.readClassBytes(Map.Entry.class);
        assertNotNull(bytes);
    }

    @Test
    void testReadClassBytes() {
        Class<?>[] clazzArray = {
                Object.class,
                Map.Entry.class,
                TreeModel.class,
//                ClassVisitor.class,
                ResourceUtils.class,
        };

        for (Class<?> clazz : clazzArray) {
            byte[] bytes = ResourceUtils.readClassBytes(clazz);
            assertNotNull(bytes);
        }
    }
}