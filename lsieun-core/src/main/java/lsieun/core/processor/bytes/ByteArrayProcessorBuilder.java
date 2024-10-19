package lsieun.core.processor.bytes;

import lsieun.core.match.path.FilePathMatch;
import lsieun.core.processor.vfs.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

public interface ByteArrayProcessorBuilder {

    static AddFileSystem<AddDir<AddFilePathMatch<AddFileProcessor>>> forDir() {
        return fs -> (first, more) -> (maxDepth, filePathMatch) -> fileProcessor ->
        {
            Path dirPath = fs.getPath(first, more);
            forDir(dirPath, maxDepth, filePathMatch, fileProcessor);
        };
    }

    private static void forDir(Path dirPath, int maxDepth, FilePathMatch filePathMatch, FileProcessor fileProcessor) {
        BiPredicate<Path, BasicFileAttributes> predicate = (path, attr) -> filePathMatch.test(path);
        try (Stream<Path> stream = Files.find(dirPath, maxDepth, predicate)) {
            stream.forEach(fileProcessor);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static AddZip<AddByteArrayProcessor> forZip() {
        return (zipPath, entry) -> func -> forZip(zipPath, entry, func);
    }

    static AddFile<AddByteArrayProcessor> forFile() {
        return file -> func -> forFile(file, func);
    }

    static AddZip<AddDstDir<AddByteArrayProcessor>> fromZip2File() {
        return ((zipPath, entry) -> dirpath -> func -> fromZip2File(zipPath, entry, dirpath, func));
    }

    static AddFile<AddZip<AddByteArrayProcessor>> fromFile2Zip() {
        return filepath -> (zipPath, entry) -> func -> fromFile2Zip(filepath, zipPath, entry, func);
    }


    static AddTank.From builder() {
        return tankFrom -> tankTo -> func -> process(tankFrom, tankTo, func);
    }

    interface AddTank {
        interface From {
            To withFromTank(ByteArrayTank tankFrom);
        }

        interface To {
            AddByteArrayProcessor withToTank(ByteArrayTank tankTo);
        }
    }


    interface ToRun {
        void run();
    }

    private static void process(ByteArrayTank tankFrom, ByteArrayTank tankTo, ByteArrayProcessor func) {
        byte[] bytes = tankFrom.read();
        byte[] newBytes = func.apply(bytes);
        tankTo.write(newBytes);
    }

    private static void forZip(Path zipPath, String entry, ByteArrayProcessor func) {
        ByteArrayTank tank = ByteArrayTank.byZipAndEntry(zipPath, entry);
        forItself(tank, func);
    }

    private static void forFile(Path path, ByteArrayProcessor func) {
        ByteArrayTank tank = ByteArrayTank.of(path);
        process(tank, tank, func);
    }

    private static void forItself(ByteArrayTank tank, ByteArrayProcessor func) {
        process(tank, tank, func);
    }

    private static void fromZip2File(Path zipPath, String entry,
                                     Path dirPath,
                                     ByteArrayProcessor func) {
        ByteArrayTank fromTank = ByteArrayTank.byZipAndEntry(zipPath, entry);
        Path newFilePath = dirPath.resolve(entry);
        ByteArrayTank toTank = ByteArrayTank.of(newFilePath);
        process(fromTank, toTank, func);
    }

    private static void fromFile2Zip(Path filePath,
                                     Path zipPath, String entry,
                                     ByteArrayProcessor func) {
        ByteArrayTank fromTank = ByteArrayTank.of(filePath);
        ByteArrayTank toTank = ByteArrayTank.byZipAndEntry(zipPath, entry);
        process(fromTank, toTank, func);
    }
}
