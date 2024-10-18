package lsieun.utils.core.bytes;

import java.nio.file.Path;

public interface ByteArrayProcessorBuilder {

    static AddZip<AddFunction> forZip() {
        return (zipPath, entry) -> func -> () -> forZip(zipPath, entry, func);
    }

    static AddFile<AddFunction> forFile() {
        return file -> func -> () -> forFile(file, func);
    }

    static AddZip<AddDir<AddFunction>> fromZip2File() {
        return ((zipPath, entry) -> dirpath -> func -> () -> fromZip2File(zipPath, entry, dirpath, func));
    }

    static AddFile<AddZip<AddFunction>> fromFile2Zip() {
        return filepath -> (zipPath, entry) -> func -> () -> fromFile2Zip(filepath, zipPath, entry, func);
    }

    interface AddZip<T> {
        T withZip(Path zipPath, String entry);
    }

    interface AddFile<T> {
        T withFile(Path filepath);
    }

    interface AddDir<T> {
        T withDir(Path dirpath);
    }


    static AddTank.From builder() {
        return tankFrom -> tankTo -> func -> () -> process(tankFrom, tankTo, func);
    }

    interface AddTank {
        interface From {
            To withFromTank(ByteArrayTank tankFrom);
        }

        interface To {
            AddFunction withToTank(ByteArrayTank tankTo);
        }
    }


    interface AddFunction {
        ToRun withFunction(ByteArrayProcessor func);
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
