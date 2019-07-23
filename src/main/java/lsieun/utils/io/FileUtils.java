package lsieun.utils.io;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {
    public static String getFilePath(String relativePath) {
        String dir = FileUtils.class.getResource("/").getPath();
        String filepath = dir + relativePath;
        return filepath;
    }

    public static String getFilePath(Class clazz, String className) {
        String path = clazz.getResource("/").getPath();
        String filepath = String.format("%s%s.class", path, className.replace('.', File.separatorChar));
        return filepath;
    }

    public static byte[] readBytes(String filename) {
        File file = new File(filename);
        if(!file.exists()) {
            return null;
        }

        InputStream in = null;

        try {
            in = new FileInputStream(file);
            in = new BufferedInputStream(in);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            IOUtils.copy(in, out);

            byte[] bytes = out.toByteArray();
            return bytes;
        } catch (FileNotFoundException e) {
            System.out.println("File Not Found: " + filename);
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            IOUtils.closeQuietly(in);
        }
        return null;
    }

    public static void writeBytes(String filename, byte[] bytes) {
        File file = new File(filename);
        File dir = file.getParentFile();
        if(!dir.exists()) {
            dir.mkdirs();
        }

        OutputStream out = null;
        try {
            out = new FileOutputStream(filename);
            out = new BufferedOutputStream(out);

            out.write(bytes);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(out);
        }
    }

    public static List<String> readLines(String filename) {
        return readLines(filename, "UTF8");
    }

    public static List<String> readLines(String filename, String charsetName) {
        File file = new File(filename);
        if(!file.exists()) {
            return null;
        }

        InputStream in = null;
        Reader reader = null;
        BufferedReader bufferReader = null;

        try {
            in = new FileInputStream(file);
            reader = new InputStreamReader(in, charsetName);
            bufferReader = new BufferedReader(reader);
            String line = null;

            List<String> list = new ArrayList();
            while((line = bufferReader.readLine()) != null) {
                list.add(line);
            }
            return list;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            IOUtils.closeQuietly(bufferReader);
            IOUtils.closeQuietly(reader);
            IOUtils.closeQuietly(in);
        }

        return null;
    }

    public static void writeLines(String filename, List<String> lines) {
        if (lines == null || lines.size() < 1) return;

        File file = new File(filename);
        File dirFile = file.getParentFile();
        if (!dirFile.exists()) {
            throw new RuntimeException("Directory Not Exist: " + dirFile.getAbsolutePath());
        }

        OutputStream out = null;
        Writer writer = null;
        BufferedWriter bufferedWriter = null;

        try {
            out = new FileOutputStream(file);
            writer = new OutputStreamWriter(out, "UTF8");
            bufferedWriter = new BufferedWriter(writer);

            for (String line : lines) {
                bufferedWriter.write(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(bufferedWriter);
            IOUtils.closeQuietly(writer);
            IOUtils.closeQuietly(out);
        }
    }

    public static byte[] readStream(final InputStream in, final boolean close) {
        if (in == null) {
            throw new IllegalArgumentException("inputStream is null!!!");
        }

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            IOUtils.copy(in, out);
            return out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (close) {
                IOUtils.closeQuietly(in);
            }
        }
        return null;
    }

    public static InputStream getInputStream(String className) {
        InputStream in = ClassLoader.getSystemResourceAsStream(className.replace('.', '/') + ".class");
        return in;
    }
}
