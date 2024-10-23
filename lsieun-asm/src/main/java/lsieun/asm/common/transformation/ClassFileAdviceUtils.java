package lsieun.asm.common.transformation;

import lsieun.annotation.mind.logic.ThinkStep;
import lsieun.asm.option.AsmCodeOptionForWrite;
import lsieun.asm.sam.consumer.AsmCodeFragment;
import lsieun.asm.sam.consumer.StdAsmCodeFragmentForPrint;
import lsieun.asm.sam.match.MethodInfoMatch;
import lsieun.asm.tag.AsmCodeTag;
import lsieun.asm.visitor.transformation.method.ClassVisitorForAdvice;
import lsieun.base.log.Logger;
import lsieun.base.log.LoggerFactory;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.util.Set;

public class ClassFileAdviceUtils {
    private static final Logger logger = LoggerFactory.getLogger(ClassFileAdviceUtils.class);

    public static byte[] applySimple(byte[] bytes, MethodInfoMatch methodMatch) {
        AsmCodeFragment methodEnter = AsmCodeFragment.of(
                StdAsmCodeFragmentForPrint.ENTER
        );
        AsmCodeFragment methodExitReturn = StdAsmCodeFragmentForPrint.EXIT_RETURN_SIMPLE;
        AsmCodeFragment methodExitThrown = StdAsmCodeFragmentForPrint.EXIT_THROWN_SIMPLE;
        return apply(bytes, methodMatch, methodEnter, methodExitReturn, methodExitThrown);
    }

    public static byte[] applyParamValues(byte[] bytes, MethodInfoMatch methodMatch) {
        AsmCodeFragment methodEnter = AsmCodeFragment.of(
                StdAsmCodeFragmentForPrint.ENTER,
                StdAsmCodeFragmentForPrint.PARAMETER_VALUES
        );
        AsmCodeFragment methodExitReturn = StdAsmCodeFragmentForPrint.EXIT_RETURN_SIMPLE;
        AsmCodeFragment methodExitThrown = StdAsmCodeFragmentForPrint.EXIT_THROWN_SIMPLE;
        return apply(bytes, methodMatch, methodEnter, methodExitReturn, methodExitThrown);
    }

    public static byte[] applyParamAndReturnValue(byte[] bytes, MethodInfoMatch methodMatch) {
        AsmCodeFragment methodEnter = AsmCodeFragment.of(
                StdAsmCodeFragmentForPrint.ENTER,
                StdAsmCodeFragmentForPrint.PARAMETER_VALUES
        );
        AsmCodeFragment methodExitReturn = StdAsmCodeFragmentForPrint.EXIT_RETURN_VALUE;
        AsmCodeFragment methodExitThrown = StdAsmCodeFragmentForPrint.EXIT_THROWN_SIMPLE;
        return apply(bytes, methodMatch, methodEnter, methodExitReturn, methodExitThrown);
    }


    public static byte[] applyStackTrace(byte[] bytes, MethodInfoMatch methodMatch) {
        AsmCodeFragment methodEnter = AsmCodeFragment.of(
                StdAsmCodeFragmentForPrint.ENTER,
                StdAsmCodeFragmentForPrint.STACK_TRACE
        );
        AsmCodeFragment methodExitReturn = StdAsmCodeFragmentForPrint.EXIT_RETURN_SIMPLE;
        AsmCodeFragment methodExitThrown = StdAsmCodeFragmentForPrint.EXIT_THROWN_SIMPLE;
        return apply(bytes, methodMatch, methodEnter, methodExitReturn, methodExitThrown);
    }

    public static byte[] applyParamAndStackTrace(byte[] bytes, MethodInfoMatch methodMatch) {
        AsmCodeFragment methodEnter = AsmCodeFragment.of(
                StdAsmCodeFragmentForPrint.ENTER,
                StdAsmCodeFragmentForPrint.PARAMETER_VALUES,
                StdAsmCodeFragmentForPrint.STACK_TRACE
        );
        AsmCodeFragment methodExitReturn = StdAsmCodeFragmentForPrint.EXIT_RETURN_SIMPLE;
        AsmCodeFragment methodExitThrown = StdAsmCodeFragmentForPrint.EXIT_THROWN_SIMPLE;
        return apply(bytes, methodMatch, methodEnter, methodExitReturn, methodExitThrown);
    }

    public static byte[] apply(byte[] bytes,
                               MethodInfoMatch methodMatch,
                               AsmCodeFragment methodEnterCodeSegment,
                               AsmCodeFragment methodExitCodeSegmentForReturn,
                               AsmCodeFragment methodExitCodeSegmentForThrown) {

        @ThinkStep("(1) COMPUTE_MAX or COMPUTE_FRAME")
        AsmCodeFragment allCodeSegment = AsmCodeFragment.of(
                methodEnterCodeSegment,
                methodExitCodeSegmentForReturn,
                methodExitCodeSegmentForThrown
        );

        Set<AsmCodeTag> tags = allCodeSegment.tags();
        AsmCodeOptionForWrite writerFlag = tags.contains(AsmCodeTag.COMPUTE_FRAMES) ?
                AsmCodeOptionForWrite.COMPUTE_FRAME :
                AsmCodeOptionForWrite.COMPUTE_MAX;
        boolean supportStackTrace = tags.contains(AsmCodeTag.STACK_TRACE);
        logger.info(() -> String.format("[writerFlag] %s", writerFlag.name()));
        logger.info(() -> String.format("[supportStackTrace] %s", supportStackTrace));

        @ThinkStep("(2) Use ASM to transform")
        //（1） ClassReader
        ClassReader cr = new ClassReader(bytes);

        //（2） ClassWriter
        ClassWriter cw = new ClassWriter(writerFlag.writerFlags);

        //（3） ClassVisitor
        ClassVisitor cv = ClassVisitorForAdvice.builder()
                .withClassVisitor(cw)
                .withStackTrace(supportStackTrace)
                .withMethodMatch(methodMatch)
                .methodEnter(methodEnterCodeSegment)
                .withMethodReturn(methodExitCodeSegmentForReturn)
                .withMethodExitThrown(methodExitCodeSegmentForThrown)
                .build();

        //（4） ClassReader + ClassVisitor
        int parsingOptions = ClassReader.EXPAND_FRAMES;
        cr.accept(cv, parsingOptions);

        //（5）return byte[]
        return cw.toByteArray();
    }
}
