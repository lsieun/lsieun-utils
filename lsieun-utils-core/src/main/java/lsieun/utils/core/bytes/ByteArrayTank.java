package lsieun.utils.core.bytes;

import lsieun.utils.core.io.file.FileContentUtils;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public interface ByteArrayTank {
    byte[] read();

    void write(byte[] data);

    class ForFileSystem implements ByteArrayTank {
        private final FileSystem fs;
        private final Path path;

        private ForFileSystem(Path path) {
            this(FileSystems.getDefault(), path);
        }

        private ForFileSystem(FileSystem fs, Path path) {
            this.fs = fs;
            this.path = path;
        }

        @Override
        public byte[] read() {
            try {
                return FileContentUtils.readBytes(path);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }

        @Override
        public void write(byte[] data) {
            try {
                FileContentUtils.writeBytes(path, data);
                close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private void close() throws IOException {
            Class<? extends FileSystem> clazz = fs.getClass();
            String typeName = clazz.getTypeName();
            if (typeName.endsWith("ZipFileSystem")) {
                fs.close();
            }
        }

        public static ForFileSystem of(Path path) {
            return new ForFileSystem(path);
        }

        public static ForFileSystem of(FileSystem fs, Path path) {
            return new ForFileSystem(fs, path);
        }

        public static ForFileSystem byZipAndEntry(Path zipPath, String first, String... more) throws IOException {
            URI zipUri = URI.create("jar:" + zipPath.toUri());
            Map<String, String> env = new HashMap<>(1);
            env.put("create", "false");
            FileSystem zipFs = FileSystems.newFileSystem(zipUri, env);
            Path path = zipFs.getPath(first, more);
            return new ForFileSystem(zipFs, path);
        }

        public static ForFileSystem byJarAndClassName(Path jarPath, String className) throws IOException {
            String entry = className.replace('.', '/') + ".class";
            return byZipAndEntry(jarPath, entry);
        }
    }
}
