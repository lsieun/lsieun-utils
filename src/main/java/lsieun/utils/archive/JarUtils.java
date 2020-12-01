package lsieun.utils.archive;

import java.io.*;
import java.net.URI;
import java.nio.file.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import lsieun.utils.RegexUtils;
import lsieun.utils.io.IOUtils;

public class JarUtils {
    public static List<String> getAllEntries(String filePath) {
        List<String> list = new ArrayList<>();
        try {
            JarFile jarFile = new JarFile(filePath);
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                list.add(entry.getName());
                //System.out.println(entry.getName());
            }
            jarFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<String> getClassEntries(String filepath) {
        List<String> list = getAllEntries(filepath);
        RegexUtils.filter(list, "^.+\\.class$");
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
            IOUtils.copy(in, bao);

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

                byte[] buf = new byte[1024];
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

    public static void updateJar(String jar_path, Map<String, String> classFileMap) {
        Map<String, String> env = new HashMap<>();
        env.put("create", "false");
        File jar_file = new File(jar_path);
        URI uri = URI.create("jar:" + jar_file.toURI());

        try (FileSystem zipfs = FileSystems.newFileSystem(uri, env)) {
            for (Map.Entry<String, String> entry : classFileMap.entrySet()) {
                Path pathInZipfile = zipfs.getPath(entry.getKey());
                Path externalTxtFile = Paths.get(entry.getValue());
                Files.copy(externalTxtFile, pathInZipfile, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException ex) {
            // ex.printStackTrace();
            // swallow exception
        }
    }
}
