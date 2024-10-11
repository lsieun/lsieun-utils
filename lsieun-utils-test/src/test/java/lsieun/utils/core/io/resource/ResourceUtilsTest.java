package lsieun.utils.core.io.resource;

import org.junit.jupiter.api.Test;

import javax.swing.tree.TreeModel;
import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ResourceUtilsTest {

    @Test
    void readFilePath() {
    }

    @Test
    void readClassBytes() throws IOException {
        byte[] bytes = ResourceUtils.readClassBytes(Map.Entry.class);
        assertNotNull(bytes);
    }

    @Test
    void testReadClassBytes() throws IOException {
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