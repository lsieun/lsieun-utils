package lsieun.utils.core.bytes;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;

public class ByteArrayThreePhase implements Runnable {
    private final ByteArrayTank tankFrom;
    private final ByteArrayTank tankTo;
    private final Function<byte[], byte[]> func;

    public ByteArrayThreePhase(ByteArrayTank tankFrom, ByteArrayTank tankTo, Function<byte[], byte[]> func) {
        this.tankFrom = tankFrom;
        this.tankTo = tankTo;
        this.func = func;
    }


    @Override
    public void run() {
        byte[] bytes = tankFrom.read();
        byte[] newBytes = func.apply(bytes);
        tankTo.write(newBytes);
    }

    public static AddFromTank builder() {
        return tankFrom -> tankTo -> func -> new ByteArrayThreePhase(tankFrom, tankTo, func);
    }

    public interface AddFromTank {
        AddToTank withFromTank(ByteArrayTank tankFrom);
    }

    public interface AddToTank {
        AddFunction withToTank(ByteArrayTank tankTo);
    }

    public interface AddFunction {
        ByteArrayThreePhase withFunction(Function<byte[], byte[]> func);
    }

    public static void forZip(Path zipPath, String entry, Function<byte[], byte[]> func) throws IOException {
        forZip(zipPath, entry, List.of(func));
    }

    public static void forZip(Path zipPath, String entry, List<Function<byte[], byte[]>> funcList) throws IOException {
        ByteArrayTank tank = ByteArrayTank.ForFileSystem.byZipAndEntry(zipPath, entry);
        forItself(tank, funcList);
    }

    public static void forFile(Path path, Function<byte[], byte[]> func) {
        forFile(path, List.of(func));
    }

    public static void forFile(Path path, List<Function<byte[], byte[]>> funcList) {
        ByteArrayTank tank = ByteArrayTank.ForFileSystem.of(path);
        forItself(tank, funcList);
    }

    public static void forItself(ByteArrayTank tank, List<Function<byte[], byte[]>> funcList) {
        fromOne2Another(tank, tank, funcList);
    }

    public static void fromZip2File(Path zipPath, String entry, Path dirPath, Function<byte[], byte[]> func) throws IOException {
        fromZip2File(zipPath, entry, dirPath, List.of(func));
    }

    public static void fromZip2File(Path zipPath, String entry, Path dirPath, List<Function<byte[], byte[]>> funcList) throws IOException {
        ByteArrayTank fromTank = ByteArrayTank.ForFileSystem.byZipAndEntry(zipPath, entry);
        Path newFilePath = dirPath.resolve(entry);
        ByteArrayTank toTank = ByteArrayTank.ForFileSystem.of(newFilePath);
        fromOne2Another(fromTank, toTank, funcList);
    }

    public static void fromFile2Zip(Path filePath, Path zipPath, String entry, Function<byte[], byte[]> func) throws IOException {
        fromFile2Zip(filePath, zipPath, entry, List.of(func));
    }

    public static void fromFile2Zip(Path filePath, Path zipPath, String entry, List<Function<byte[], byte[]>> funcList) throws IOException {
        ByteArrayTank fromTank = ByteArrayTank.ForFileSystem.of(filePath);
        ByteArrayTank toTank = ByteArrayTank.ForFileSystem.byZipAndEntry(zipPath, entry);
        fromOne2Another(fromTank, toTank, funcList);
    }

    public static void fromOne2Another(ByteArrayTank fromTank, ByteArrayTank toTank, List<Function<byte[], byte[]>> funcList) {
        ByteArrayThreePhase.builder()
                .withFromTank(fromTank)
                .withToTank(toTank)
                .withFunction(
                        bytes -> {
                            for (Function<byte[], byte[]> func : funcList) {
                                bytes = func.apply(bytes);
                            }
                            return bytes;
                        }
                ).run();
    }
}
