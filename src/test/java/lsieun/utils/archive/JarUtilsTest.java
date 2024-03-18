package lsieun.utils.archive;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;


public class JarUtilsTest {

    @Test
    public void testGetAllEntries() {
        String user_dir = "/home/liusen/Workspace/git-repo/aedi-msa";
        String filepath = user_dir + File.separator + "lib" + File.separator + "idea.jar";
        System.out.println("file://" + filepath);

        File file = new File(filepath);
        if (!file.exists()) return;

        List<String> list = JarUtils.getClassEntries(filepath);
        for(String item : list) {
            System.out.println(item);
        }
    }

}