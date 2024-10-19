package lsieun.base.archive.zip;

import lsieun.annotation.mind.blueprint.Intention;
import lsieun.base.ds.pair.Pair;
import lsieun.base.log.Logger;
import lsieun.base.log.LoggerFactory;

import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class ZipFindNioUtilsForMultiple {
    private static final Logger logger = LoggerFactory.getLogger(ZipFindNioUtilsForMultiple.class);

    @Intention({
            "Pair 中 String 存储的是 com/abc/Xyz.class",
            "Pair 中 Path 存储是 jar 包的路径"
    })
    public static List<Pair<String, Path>> findPairList(List<Path> pathList, List<String> entryList) {
        Objects.requireNonNull(pathList);
        Objects.requireNonNull(entryList);

        if (pathList.isEmpty()) {
            return Collections.emptyList();
        }

        int size = pathList.size();

        logger.debug(() -> String.format("Total Jars: %d", size));


        List<Pair<String, Path>> resultList = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            Path jarPath = pathList.get(i);
            int num = i + 1;
            logger.debug(() -> String.format("[PROCESS] %03d - %s", num, jarPath));

            URI zipUri = URI.create("jar:" + jarPath.toUri());

            Map<String, String> env = new HashMap<>(1);
            env.put("create", "false");

            try (FileSystem zipFs = FileSystems.newFileSystem(zipUri, env)) {
                for (String entry : entryList) {

                    Path entryPath = zipFs.getPath(entry);
                    if (Files.exists(entryPath)) {
                        resultList.add(new Pair<>(entry, jarPath));
                    }
                }
            } catch (Exception e) {
                String msg = String.format("something is wrong: %s", e.getMessage());
                System.out.println(msg);
            }
        }
        return resultList;
    }

    public static List<Pair<String, Path>> findPairListByClassNames(List<Path> pathList, String[] classnames) {
        List<String> entryList = Arrays.stream(classnames)
                .map(name -> name.replace(".", "/") + ".class")
                .toList();
        return findPairList(pathList, entryList);
    }

    public static List<Path> findFileList(List<Path> pathList, List<String> entryList) {
        List<Pair<String, Path>> pairList = findPairList(pathList, entryList);
        return pairList.stream().map(Pair::second).distinct().toList();
    }

    public static List<Path> findFileListByClassNames(List<Path> pathList, String[] classnames) {
        List<Pair<String, Path>> pairList = findPairListByClassNames(pathList, classnames);
        return pairList.stream().map(Pair::second).distinct().toList();
    }
}
