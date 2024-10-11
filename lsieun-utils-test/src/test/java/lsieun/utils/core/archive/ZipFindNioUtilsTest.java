package lsieun.utils.core.archive;

import lsieun.utils.core.archive.zip.ZipFindNioUtils;
import lsieun.utils.core.coll.ListUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

class ZipFindNioUtilsTest {
    @Test
    void testGetEntryListByExtension() throws IOException {
        String filepath = "C:\\Users\\liusen\\.m2\\repository\\net\\bytebuddy\\byte-buddy-dep\\1.14.18\\byte-buddy-dep-1.14.18.jar";
        Path jarPath = Paths.get(filepath);
        List<String> entryList = ZipFindNioUtils.findEntryListByExtension(jarPath, ".class");
        ListUtils.print(entryList);
    }

    @Test
    void testGetClassList() throws IOException {
        String filepath = "C:\\Users\\liusen\\.m2\\repository\\net\\bytebuddy\\byte-buddy-dep\\1.14.18\\byte-buddy-dep-1.14.18.jar";
        Path jarPath = Paths.get(filepath);
        List<String> entryList = ZipFindNioUtils.findClassList(jarPath);
        ListUtils.print(entryList);
    }

    @Test
    void testGetClassNameList() throws IOException {
        String filepath = "C:\\Users\\liusen\\.m2\\repository\\net\\bytebuddy\\byte-buddy-dep\\1.14.18\\byte-buddy-dep-1.14.18.jar";
        Path jarPath = Paths.get(filepath);
        List<String> classNameList = ZipFindNioUtils.findClassList(jarPath);
        ListUtils.print(classNameList);
    }
}