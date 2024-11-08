package lsieun.base.io.file;

import lsieun.annotation.type.UtilityClass;
import lsieun.base.number.ByteUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@UtilityClass
public class FileUtils {
    public static final int BUFFER_SIZE = 16 * 1024;

    public static File getTempDirectory() {
        return new File(System.getProperty("java.io.tmpdir"));
    }

    public static String getFilePath(String relativePath) {
        String dir = Objects.requireNonNull(FileUtils.class.getResource("/")).getPath();
        String filepath = dir + relativePath;
        if (filepath.contains(":")) {
            return filepath.substring(1);
        }
        return filepath;
    }

    public static String getFilePath(Class<?> clazz, String className) {
        String path = Objects.requireNonNull(clazz.getResource("/")).getPath();
        return String.format("%s%s.class", path, className.replace('.', File.separatorChar));
    }

    public static boolean exists(String filepath) {
        if (filepath == null) {
            return false;
        }

        File file = new File(filepath);
        return file.exists() && file.isFile();
    }

    public static byte[] readBytes(String filepath) {
        File file = new File(filepath);
        if (!file.exists()) {
            throw new IllegalArgumentException("File Not Exist: " + filepath);
        }

        try (
                FileInputStream fis = new FileInputStream(file);
                BufferedInputStream bis = new BufferedInputStream(fis)
        ) {
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            byte[] buf = new byte[BUFFER_SIZE];
            int len;
            while ((len = bis.read(buf)) != -1) {
                bao.write(buf, 0, len);
            }
            return bao.toByteArray();
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    public static void writeBytes(String filepath, byte[] bytes) {
        File file = new File(filepath);
        File dirFile = file.getParentFile();
        mkdirs(dirFile);

        try (OutputStream out = Files.newOutputStream(Paths.get(filepath));
             BufferedOutputStream buff = new BufferedOutputStream(out)) {
            buff.write(bytes);
            buff.flush();
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    public static List<String> readLines(String filepath) {
        return readLines(filepath, "UTF8");
    }

    public static List<String> readLines(String filepath, String charsetName) {
        File file = new File(filepath);
        if (!file.exists()) {
            throw new IllegalArgumentException("File Not Exist: " + filepath);
        }

        try (
                InputStream in = Files.newInputStream(file.toPath());
                Reader reader = new InputStreamReader(in, charsetName);
                BufferedReader bufferReader = new BufferedReader(reader)
        ) {
            List<String> list = new ArrayList<>();
            String line;
            while ((line = bufferReader.readLine()) != null) {
                list.add(line);
            }
            return list;
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    public static void writeLines(String filepath, List<String> lines) {
        if (lines == null || lines.isEmpty()) {
            return;
        }

        File file = new File(filepath);
        File dirFile = file.getParentFile();
        mkdirs(dirFile);

        try (
                OutputStream out = Files.newOutputStream(file.toPath());
                Writer writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);
                BufferedWriter bufferedWriter = new BufferedWriter(writer)
        ) {
            for (String line : lines) {
                bufferedWriter.write(line);
                bufferedWriter.newLine();
            }
            bufferedWriter.flush();
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    public static void mkdirs(File dirFile) {
        boolean fileExists = dirFile.exists();

        if (fileExists && dirFile.isDirectory()) {
            return;
        }

        if (fileExists && dirFile.isFile()) {
            throw new RuntimeException("Not A Directory: " + dirFile);
        }

        if (!fileExists) {
            boolean flag = dirFile.mkdirs();
            if (!flag) {
                throw new RuntimeException("Create Directory Failed: " + dirFile.getAbsolutePath());
            }
        }
    }

    public static void copy(String srcPath, String destPath) {
        File srcFile = new File(srcPath);
        if (!srcFile.exists()) {
            throw new IllegalArgumentException("srcPath is not valid: " + srcPath);
        }

        File destFile = new File(destPath);
        File dirFile = destFile.getParentFile();
        mkdirs(dirFile);

        try (
                FileInputStream fis = new FileInputStream(srcFile);
                BufferedInputStream bis = new BufferedInputStream(fis);
                FileOutputStream fos = new FileOutputStream(destFile);
                BufferedOutputStream bos = new BufferedOutputStream(fos)
        ) {
            byte[] buf = new byte[BUFFER_SIZE];
            int len;
            while ((len = bis.read(buf)) != -1) {
                bos.write(buf, 0, len);
            }
            bos.flush();
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    public static void negateCopy(String srcPath, String destPath) {
        File srcFile = new File(srcPath);
        if (!srcFile.exists()) {
            throw new IllegalArgumentException("srcPath is not valid: " + srcPath);
        }

        File destFile = new File(destPath);
        File dirFile = destFile.getParentFile();
        mkdirs(dirFile);

        try (
                FileInputStream fis = new FileInputStream(srcFile);
                BufferedInputStream bis = new BufferedInputStream(fis);
                FileOutputStream fos = new FileOutputStream(destFile);
                BufferedOutputStream bos = new BufferedOutputStream(fos)
        ) {
            byte[] buf = new byte[BUFFER_SIZE];
            int len;
            while ((len = bis.read(buf)) != -1) {
                for (int i = 0; i < len; i++) {
                    byte b = buf[i];
                    buf[i] = ByteUtils.negate(b);
                }
                bos.write(buf, 0, len);
            }
            bos.flush();
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    public static byte[] readClassBytes(String className) {
        InputStream in = getInputStream(className);
        return readStream(in);
    }

    public static byte[] readStream(final InputStream in) {
        if (in == null) {
            throw new IllegalArgumentException("inputStream is null!!!");
        }

        try (in) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[BUFFER_SIZE];
            int len;
            while ((len = in.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
            return out.toByteArray();
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
        //
    }

    public static InputStream getInputStream(String className) {
        return ClassLoader.getSystemResourceAsStream(className.replace('.', '/') + ".class");
    }

    // region 文件内容
    public static long compareFilesByByte(Path path1, Path path2) throws IOException {
        try (BufferedInputStream fis1 = new BufferedInputStream(Files.newInputStream(path1.toFile().toPath()));
             BufferedInputStream fis2 = new BufferedInputStream(Files.newInputStream(path2.toFile().toPath()))) {

            int ch = 0;
            long pos = 1;
            while ((ch = fis1.read()) != -1) {
                if (ch != fis2.read()) {
                    return pos;
                }
                pos++;
            }
            if (fis2.read() == -1) {
                return -1;
            }
            else {
                return pos;
            }
        }
    }
    // endregion

}
