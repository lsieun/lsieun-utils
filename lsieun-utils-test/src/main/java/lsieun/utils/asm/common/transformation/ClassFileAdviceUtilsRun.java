package lsieun.utils.asm.common.transformation;

import lsieun.utils.asm.core.AsmTypeNameUtils;
import lsieun.utils.asm.description.MemberDesc;
import lsieun.utils.asm.match.MethodInfoMatch;
import lsieun.utils.core.bytes.ByteArrayProcessor;
import lsieun.utils.core.bytes.ByteArrayProcessorBuilder;

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
                .withFunction(func);
    }
}