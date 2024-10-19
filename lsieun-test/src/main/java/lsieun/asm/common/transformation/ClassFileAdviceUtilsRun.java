package lsieun.asm.common.transformation;

import lsieun.asm.core.AsmTypeNameUtils;
import lsieun.asm.description.MemberDesc;
import lsieun.asm.match.MethodInfoMatch;
import lsieun.core.processor.bytes.ByteArrayProcessor;
import lsieun.core.processor.bytes.ByteArrayProcessorBuilder;

import java.io.IOException;
import java.nio.file.Path;

public class ClassFileAdviceUtilsRun {
    public static void main(String[] args) throws IOException {
        addAdvice();
    }

    public static void addAdvice() throws IOException {
        // (1) jar and entry
        Path jarPath = Path.of("...");
        MemberDesc memberDesc = MemberDesc.of("com.abc.Xyz::b:()V");
        String entry = AsmTypeNameUtils.toJarEntryName(memberDesc.owner());

        // (2) match: method
        String methodName = memberDesc.name();
        String methodDesc = memberDesc.desc();
        MethodInfoMatch methodMatch = MethodInfoMatch.byMethodNameAndDesc(methodName, methodDesc);

        // (3) mapping: bytes --> bytes
        ByteArrayProcessor func = bytes -> ClassFileAdviceUtils.applySimple(
                bytes, methodMatch
        );

        // (4) transform
        ByteArrayProcessorBuilder.forZip()
                .withZip(jarPath, entry)
                .withByteArrayProcessor(func);
    }
}
