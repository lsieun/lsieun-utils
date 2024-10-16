package lsieun.utils.asm.common;

import lsieun.utils.core.io.resource.ResourceUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.util.ASMifier;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 * <pre>
 *                                                          ┌─── toAsmCode() ───┼─── printASMCode()
 * ClassFileTextUtils ───┼─── toBytes() ───┼─── toText() ───┤
 *                                                          └─── toAsmText() ───┼─── printASMText()
 * </pre>
 */
public class ClassFileTextUtils {
    static final int DEFAULT_PARSING_OPTIONS = ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES;

    public static byte[] toBytes(byte[] bytes, int parsingOptions, boolean asmCode) {
        // 第一步，创建ClassReader
        ClassReader cr = new ClassReader(bytes);

        // 第二步，创建ClassVisitor
        Printer printer = asmCode ? new ASMifier() : new Textifier();
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        PrintWriter printWriter = new PrintWriter(bao, true, StandardCharsets.UTF_8);
        ClassVisitor cv = new TraceClassVisitor(null, printer, printWriter);

        // 第三步，连接ClassReader和ClassVisitor
        cr.accept(cv, parsingOptions);

        return bao.toByteArray();
    }

    public static String toText(byte[] bytes, int parsingOptions, boolean asmCode) {
        byte[] txtBytes = toBytes(bytes, parsingOptions, asmCode);
        return new String(txtBytes, StandardCharsets.UTF_8);
    }

    public static String toAsmCode(byte[] bytes) {
        return toText(bytes, DEFAULT_PARSING_OPTIONS, true);
    }

    public static String toAsmText(byte[] bytes) {
        return toText(bytes, DEFAULT_PARSING_OPTIONS, false);
    }

    public static void printAsmCode(byte[] bytes) {
        String text = toAsmCode(bytes);
        System.out.println(text);
    }

    public static void printAsmCode(Class<?> clazz) {
        byte[] bytes = ResourceUtils.readClassBytes(clazz);
        printAsmCode(bytes);
    }

    public static void printAsmText(byte[] bytes) {
        String text = toAsmText(bytes);
        System.out.println(text);
    }

    public static void printAsmText(Class<?> clazz) {
        byte[] bytes = ResourceUtils.readClassBytes(clazz);
        printAsmText(bytes);
    }
}
