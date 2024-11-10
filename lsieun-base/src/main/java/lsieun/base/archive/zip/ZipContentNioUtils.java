package lsieun.base.archive.zip;

import lsieun.base.ds.box.pair.Pair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.function.BiConsumer;

/**
 * <pre>
 *                                      ┌─── single.entry ─────┼─── readEntryBytes()
 *                       ┌─── read ─────┤
 *                       │              └─── multiple.entry ───┼─── readEntryBytesList() ───┼─── readClassBytesList()
 * ZipContentNioUtils ───┤
 *                       │                                       ┌─── updateZipByMapOfPath()
 *                       └─── update ───┼─── updateZipByMap() ───┤
 *                                                               └─── updateZipByMapOfByteArray()
 * </pre>
 */
public class ZipContentNioUtils {

    // region read
    public static byte[] readEntryBytes(Path zipPath, String entry) throws IOException {
        Objects.requireNonNull(zipPath, "zipPath");
        Objects.requireNonNull(entry, "entry");

        return ZipNioUtils.process(zipPath, false, zipFs -> {
            Path path = zipFs.getPath(entry);
            try {
                return Files.readAllBytes(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static List<Pair<String, byte[]>> readEntryBytesList(Path zipPath, List<String> entryList) throws IOException {
        Objects.requireNonNull(zipPath, "zipPath");
        Objects.requireNonNull(entryList, "entryList must not be null");
        if (entryList.isEmpty()) {
            return Collections.emptyList();
        }

        return ZipNioUtils.process(zipPath, false, zipFs -> {
            List<Pair<String, byte[]>> list = new ArrayList<>();
            try {
                for (String entry : entryList) {
                    Path path = zipFs.getPath(entry);
                    if (Files.exists(path)) {
                        byte[] bytes = Files.readAllBytes(path);
                        list.add(new Pair<>(entry, bytes));
                    }
                    else {
                        list.add(new Pair<>(entry, null));
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            return list;
        });
    }


    public static List<Pair<String, byte[]>> readClassBytesList(Path jarPath) throws IOException {
        Objects.requireNonNull(jarPath, "jarPath must not be null");
        List<String> classList = ZipFindNioUtils.findClassList(jarPath);
        List<String> entryList = classList.stream().toList();
        return readEntryBytesList(jarPath, entryList);
    }
    // endregion

    // region update
    public static <T> void updateZipByMap(Path jarPath, Map<String, T> map, BiConsumer<Path, T> biConsumer) throws IOException {

        ZipNioUtils.consume(jarPath, zipFs -> {
            try {
                for (Map.Entry<String, T> entry : map.entrySet()) {
                    Path internalPathInZip = zipFs.getPath(entry.getKey());
                    T value = entry.getValue();
                    if (!Files.exists(internalPathInZip)) {
                        Path parent = internalPathInZip.getParent();
                        Files.createDirectories(parent);
                    }
                    biConsumer.accept(internalPathInZip, value);
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

        });
    }

    public static void updateZipByMapOfPath(Path zipPath, Map<String, Path> classFileMap) throws IOException {

        updateZipByMap(zipPath, classFileMap, (internalPathInZip, externalPath) -> {
            try {
                Files.copy(externalPath, internalPathInZip, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static void updateZipByMapOfByteArray(Path zipPath, Map<String, byte[]> classFileMap) throws IOException {

        updateZipByMap(zipPath, classFileMap, (zipEntryPath, bytes) -> {
            try {
                Files.write(zipEntryPath, bytes);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

    }
    // endregion

}
