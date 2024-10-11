package lsieun.utils.core.archive;

import lsieun.utils.core.archive.jar.JarUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;


class JarUtilsTest {

    @Test
    void testGetAllEntries() {
        String user_dir = "/home/liusen/Workspace/git-repo/aedi-msa";
        String filepath = user_dir + File.separator + "lib" + File.separator + "idea.jar";
        System.out.println("file://" + filepath);

        File file = new File(filepath);
        if (!file.exists()) return;

        List<String> list = JarUtils.getClassEntries(filepath);
        for (String item : list) {
            System.out.println(item);
        }
    }

}