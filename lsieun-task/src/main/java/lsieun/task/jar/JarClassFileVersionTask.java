package lsieun.task.jar;

import lsieun.base.archive.zip.ZipContentNioUtils;
import lsieun.base.archive.zip.ZipFindNioUtils;
import lsieun.base.ds.pair.Pair;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class JarClassFileVersionTask {

    public static List<Integer> readAllClassFileVersion(Path zipPath) throws IOException {
        List<String> classList = ZipFindNioUtils.findClassList(zipPath);
        List<String> entryList = classList.stream().toList();
        List<Pair<String, byte[]>> pairList = ZipContentNioUtils.readEntryBytesList(zipPath, entryList);

        List<Integer> list = new ArrayList<>();
        for (Pair<String, byte[]> pair : pairList) {
            byte[] bytes = pair.second();
            if (bytes == null) {
                continue;
            }
            if (bytes.length < 8) {
                continue;
            }
            if (
                    (bytes[0] & 0xFF) != 0xCA ||
                            (bytes[1] & 0xFF) != 0xFE ||
                            (bytes[2] & 0xFF) != 0xBA ||
                            (bytes[3] & 0xFF) != 0xBE
            ) {
                continue;
            }
            byte b1 = bytes[6];
            byte b2 = bytes[7];
            int majorVersion = (b1 & 0xFF) << 8 | (b2 & 0xFF);
            int jdkVersion = majorVersion - 44;
            if (!list.contains(jdkVersion)) {
                list.add(jdkVersion);
            }
        }

        Collections.sort(list);
        return list;
    }

    public static List<Pair<Path, Integer>> readAllClassFileVersionInDir(List<Path> jarPathList) throws IOException {
        Objects.requireNonNull(jarPathList);
        if (jarPathList.isEmpty()) {
            return Collections.emptyList();
        }

        List<Pair<Path, Integer>> list = new ArrayList<>();
        for (Path jarPath : jarPathList) {
            List<Integer> versionList = readAllClassFileVersion(jarPath);
            if (!versionList.isEmpty()) {
                for (Integer version : versionList) {
                    list.add(new Pair<>(jarPath, version));
                }
            }
        }
        return list;
    }

}