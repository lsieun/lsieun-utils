package lsieun.asm.common.transformation;

import lsieun.asm.consumer.InsnInvokeConsumer;
import lsieun.asm.consumer.InsnInvokeConsumerGallery;
import lsieun.asm.core.AsmTypeNameUtils;
import lsieun.asm.description.MemberDesc;
import lsieun.asm.match.InsnInvokeMatch;
import lsieun.asm.match.MethodInfoMatch;
import lsieun.core.processor.bytes.ByteArrayProcessor;
import lsieun.core.processor.bytes.ByteArrayProcessorBuilder;

import java.io.IOException;
import java.nio.file.Path;

public class ClassFileModifyUtilsRun {
    public static void main(String[] args) throws IOException {
        modifyInsn();
    }

    public static void modifyInsn() {
        // (1) jar and entry
        Path jarPath = Path.of("...");
        MemberDesc memberDesc = MemberDesc.of("com.abc.Xyz::b:()V");
        String entry = AsmTypeNameUtils.toJarEntryName(memberDesc.owner());

        // (2) match: method and insn
        String methodName = memberDesc.name();
        String methodDesc = memberDesc.desc();
        MethodInfoMatch methodMatch = MethodInfoMatch.byMethodNameAndDesc(methodName, methodDesc);
        InsnInvokeMatch insnInvokeMatch = InsnInvokeMatch.All.INSTANCE;

        // (3) mapping: bytes --> bytes
        InsnInvokeConsumer insnInvokeConsumer = InsnInvokeConsumerGallery.printInvokeMethodInsnParamsAndReturn();
        ByteArrayProcessor func = bytes -> ClassFileModifyUtils.modifyInsnInvoke(
                bytes, methodMatch, insnInvokeMatch, insnInvokeConsumer);

        // (4) transform
        ByteArrayProcessorBuilder.forZip()
                .withZip(jarPath, entry)
                .withByteArrayProcessor(func);
    }
}
