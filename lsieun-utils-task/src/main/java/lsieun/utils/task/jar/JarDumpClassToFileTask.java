package lsieun.utils.task.jar;

import lsieun.utils.annotation.method.MethodParamExample;
import lsieun.utils.asm.common.ClassFileTextUtils;
import lsieun.utils.asm.core.AsmTypeNameUtils;
import lsieun.utils.asm.match.MethodInfoMatch;
import lsieun.utils.asm.common.ClassFileModifyUtils;
import lsieun.utils.asm.visitor.transformation.modify.method.MethodBodyInfoType;
import lsieun.utils.core.archive.zip.ZipContentNioUtils;
import lsieun.utils.core.io.file.FileContentUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.Set;

public class JarDumpClassToFileTask {
    public static void dumpOrigin(Path jarPath,
                                  @MethodParamExample({"com/abc/Xyz.class"}) String entry,
                                  Path targetDirPath) throws IOException {
        byte[] bytes = ZipContentNioUtils.readEntryBytes(jarPath, entry);
        Path filepath = targetDirPath.resolve(entry);
        FileContentUtils.writeBytes(filepath, bytes);
    }

    public static void dumpClassAsmText(Path jarPath, String classname, Path targetDirPath,
                                        int parsingOptions, boolean asmCode) throws IOException {
        String entry = AsmTypeNameUtils.toJarEntryName(classname);
        byte[] bytes = ZipContentNioUtils.readEntryBytes(jarPath, entry);

        byte[] newBytes = ClassFileTextUtils.toBytes(bytes, parsingOptions, asmCode);

        Path filepath = targetDirPath.resolve(classname + ".txt");
        FileContentUtils.writeBytes(filepath, newBytes);
    }

    public static void dumpAndAddStackTrace(Path jarPath,
                                            @MethodParamExample({"com/abc/Xyz.class"}) String entry,
                                            MethodInfoMatch methodMatch,
                                            Path targetDirPath) throws IOException {
        EnumSet<MethodBodyInfoType> options = EnumSet.of(
                MethodBodyInfoType.ENTER,
                MethodBodyInfoType.PARAMETER_VALUES,
                MethodBodyInfoType.STACK_TRACE,
                MethodBodyInfoType.EXIT
        );
        dumpAndAddStackTrace(jarPath, entry, methodMatch, options, targetDirPath);
    }

    public static void dumpAndAddStackTrace(Path jarPath,
                                            @MethodParamExample({"com/abc/Xyz.class"}) String entry,
                                            MethodInfoMatch methodMatch,
                                            Set<MethodBodyInfoType> options,
                                            Path targetDirPath) throws IOException {
        byte[] bytes = ZipContentNioUtils.readEntryBytes(jarPath, entry);
        byte[] newBytes = ClassFileModifyUtils.printMethodInfo(bytes, methodMatch, options);
        Path filepath = targetDirPath.resolve(entry);
        FileContentUtils.writeBytes(filepath, newBytes);
    }
}