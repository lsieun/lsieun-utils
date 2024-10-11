package lsieun.utils.core.archive.zip;

import lsieun.utils.annotation.method.MethodParamExample;
import lsieun.utils.core.log.Logger;
import lsieun.utils.core.log.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <pre>
 *                                                                 ┌─── findEntryListByExtension()
 *                    ┌─── single.jar ─────┼─── findEntryList() ───┤
 *                    │                                            └─── findClassList() ──────────────┼─── findClassNameList()
 * ZipFindNioUtils ───┤
 *                    │                                           ┌─── findPairListByClassNames()
 *                    └─── multiple.jar ───┼─── findPairList() ───┤
 *                                                                └─── findFileList()
 * </pre>
 */
public class ZipFindNioUtils {
    private static final Logger logger = LoggerFactory.getLogger(ZipFindNioUtils.class);

    public static List<String> findEntryList(Path jarPath,
                                             BiPredicate<Path, BasicFileAttributes> predicate) throws IOException {
        if (jarPath == null) {
            throw new IllegalArgumentException("jarPath is null");
        }
        if (!Files.exists(jarPath)) {
            throw new IllegalArgumentException("jar does not exist: " + jarPath);
        }
        if (!Files.isRegularFile(jarPath)) {
            throw new IllegalArgumentException("jar is not a file: " + jarPath);
        }

        URI zipUri = URI.create("jar:" + jarPath.toUri());

        Map<String, String> env = new HashMap<>(1);
        env.put("create", "false");

        try (FileSystem zipfs = FileSystems.newFileSystem(zipUri, env)) {
            Path dirPath = zipfs.getPath("/");

            try (Stream<Path> stream = Files.find(dirPath, Integer.MAX_VALUE, predicate)) {
                return stream
                        .map(Path::toString)
                        .sorted()
                        .collect(Collectors.toList());
            }
        }
    }


    public static List<String> findEntryListByExtension(Path jarPath,
                                                        @MethodParamExample({".class", ".java"}) String ext) throws IOException {
        BiPredicate<Path, BasicFileAttributes> predicate = (path, attrs) ->
                attrs.isRegularFile() && path.getFileName().toString().endsWith(ext);
        return findEntryList(jarPath, predicate);
    }

    public static List<String> findClassList(Path jarPath) throws IOException {
        BiPredicate<Path, BasicFileAttributes> predicate = (path, attrs) -> {
            if (!attrs.isRegularFile()) {
                return false;
            }

            String filename = path.getFileName().toString();

            if (!filename.endsWith(".class")) {
                return false;
            }

            if (filename.equals("package-info.class")) {
                return false;
            }

            if (filename.contains("$")) {
                return false;
            }

            return true;
        };

        return findEntryList(jarPath, predicate);
    }


    public static List<String> findClassNameList(Path jarPath) throws IOException {
        List<String> classList = findClassList(jarPath);
        int size = classList.size();

        List<String> nameList = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            String entry = classList.get(i);
            int index = entry.lastIndexOf(".");
            if (entry.startsWith("/")) {
                entry = entry.substring(1, index);
            }
            else {
                entry = entry.substring(0, index);
            }
            String name = entry.replace('/', '.');
            nameList.add(name);
        }
        Collections.sort(nameList);
        return nameList;
    }
}
