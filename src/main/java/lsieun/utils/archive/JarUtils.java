package lsieun.utils.archive;

import java.io.*;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarUtils {
    public static final int BUFFER_SIZE = 16 * 1024;

    public static List<String> getAllEntries(String filePath) {
        List<String> list = new ArrayList<>();
        try {
            JarFile jarFile = new JarFile(filePath);
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                list.add(entry.getName());
            }
            jarFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<String> getClassEntries(String filepath) {
        List<String> list = getAllEntries(filepath);
        int size = list.size();
        for (int i = size - 1; i >= 0; i--) {
            String jarItem = list.get(i);
            if (jarItem != null && jarItem.endsWith(".class")) {
                continue;
            }
            list.remove(i);
        }
        return list;
    }

    /**
     * Get Fully-Qualified Class Name
     *
     * @param entryName 例如，java/lang/Object.class
     * @return 例如java.lang.Object
     */
    public static String getFQCN(String entryName) {
        return entryName.replace(".class", "").replace("/", ".");
    }

    public static byte[] readClass(String jarPath, String entryName) {
        try (
                JarFile jarFile = new JarFile(jarPath);
                InputStream in = jarFile.getInputStream(jarFile.getJarEntry(entryName))
        ) {
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            byte[] buffer = new byte[BUFFER_SIZE];
            int length;
            while ((length = in.read(buffer)) != -1) {
                bao.write(buffer, 0, length);
            }
            return bao.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Map<String, ByteArrayOutputStream> getAllClasses(String jarPath, List<String> entryList) {
        if (jarPath == null || "".equals(jarPath)) return null;
        if (entryList == null || entryList.size() < 1) return null;

        Map<String, ByteArrayOutputStream> map = new HashMap<>();
        JarFile jarFile = null;
        try {
            jarFile = new JarFile(jarPath);
            for (String entryName : entryList) {
                JarEntry entry = jarFile.getJarEntry(entryName);
                InputStream in = jarFile.getInputStream(entry);

                in = new BufferedInputStream(in);

                ByteArrayOutputStream out = new ByteArrayOutputStream();

                byte[] buf = new byte[BUFFER_SIZE];
                int len;
                while ((len = in.read(buf)) != -1) {
                    out.write(buf, 0, len);
                }

                in.close();

                String className = getFQCN(entryName);
                map.put(className, out);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (jarFile != null) {
                try {
                    jarFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return map;
    }

    public static void updateJar(String jarPath, Map<String, String> classFileMap) {
        Map<String, String> env = new HashMap<>();
        env.put("create", "false");
        File jar_file = new File(jarPath);
        URI uri = URI.create("jar:" + jar_file.toURI());

        try (FileSystem zipfs = FileSystems.newFileSystem(uri, env)) {
            for (Map.Entry<String, String> entry : classFileMap.entrySet()) {
                Path pathInZipfile = zipfs.getPath(entry.getKey());
                Path externalTxtFile = Paths.get(entry.getValue());
                if (!Files.exists(pathInZipfile)) {
                    Files.createDirectories(pathInZipfile);
                }
                Files.copy(externalTxtFile, pathInZipfile, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException ex) {
             ex.printStackTrace();
        }
    }

    public static void updateJar(String jarPath, String item, String filepath) {
        Map<String, String> map = new HashMap<>();
        map.put(item, filepath);
        updateJar(jarPath, map);
    }
}
