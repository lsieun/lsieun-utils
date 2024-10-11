package lsieun.utils.asm.common;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.util.ASMifier;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public class ClassFileTextUtils {
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
}
