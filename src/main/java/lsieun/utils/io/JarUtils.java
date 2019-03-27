package lsieun.utils.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarUtils {
    /**
     *
     * @param jarPath jar包的路径，例如："/usr/local/jdk8/jre/lib/rt.jar"
     * @param entryName  class文件路径，例如："java/lang/Object.class"
     * @return
     */
    public static byte[] readBytes(String jarPath, String entryName) {
        // System.out.println("Read Jar: " + jarPath);

        JarFile jarFile = null;
        InputStream in = null;
        ByteArrayOutputStream out = null;

        try {
            jarFile = new JarFile(jarPath);
            JarEntry entry = jarFile.getJarEntry(entryName);
            in = jarFile.getInputStream(entry);

            out = new ByteArrayOutputStream();
            IOUtils.copy(in, out);

            byte[] bytes = out.toByteArray();
            return bytes;

        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(jarFile);
        }
        return null;
    }
}
