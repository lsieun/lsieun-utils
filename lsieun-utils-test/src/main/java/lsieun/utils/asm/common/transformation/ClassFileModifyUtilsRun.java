package lsieun.utils.asm.common.transformation;

import lsieun.utils.asm.consumer.InsnInvokeConsumer;
import lsieun.utils.asm.consumer.InsnInvokeConsumerGallery;
import lsieun.utils.asm.core.AsmTypeNameUtils;
import lsieun.utils.asm.description.MemberDesc;
import lsieun.utils.asm.match.InsnInvokeMatch;
import lsieun.utils.asm.match.MethodInfoMatch;
import lsieun.utils.core.bytes.ByteArrayProcessor;
import lsieun.utils.core.bytes.ByteArrayProcessorBuilder;

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
                .withFunction(func);
    }
}
