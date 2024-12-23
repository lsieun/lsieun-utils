package lsieun.asm.sam.consumer;

import lsieun.asm.insn.code.AsmInsnUtilsForPrint;
import lsieun.asm.insn.code.AsmInsnUtilsForThread;
import lsieun.asm.tag.AsmCodeTag;
import lsieun.asm.tag.AsmCodeTagCarrier;
import lsieun.asm.visitor.transformation.method.MethodBodyInfoType;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.MethodVisitor;

import java.util.*;

/**
 * <pre>
 *                                   ┌─── non-static ───┼─── implement() [SAM]
 *                    ┌─── method ───┤
 *                    │              │                  ┌─── print()
 *                    │              └─── static ───────┤
 * AsmCodeFragment ───┤                                 └─── of()
 *                    │
 *                    │              ┌─── NoOp ────┼─── INSTANCE
 *                    └─── enum ─────┤
 *                                   │             ┌─── CURRENT_THREAD_INFO
 *                                   └─── Print ───┤
 *                                                 └─── ALL_THREAD_INFO
 * </pre>
 *
 * @see StdAsmCodeFragmentForPrint
 */
@FunctionalInterface
public interface AsmCodeFragment extends AsmCodeTagCarrier.ForMax {

    void implement(MethodVisitor mv, String currentType,
                   int methodAccess, String methodName, String methodDesc,
                   String signature, String[] exceptions);

    static AsmCodeFragment print(String str) {
        return new AsmCodeFragment() {
            @Override
            public Set<AsmCodeTag> tags() {
                EnumSet<AsmCodeTag> tagSet = EnumSet.of(AsmCodeTag.PRINT);
                tagSet.addAll(AsmCodeFragment.super.tags());
                return tagSet;
            }

            @Override
            public void implement(MethodVisitor mv, String currentType,
                                  int methodAccess, String methodName, String methodDesc,
                                  String signature, String[] exceptions) {
                AsmInsnUtilsForPrint.printMessage(mv, str);
            }
        };
    }

    static AsmCodeFragment print(@NotNull Set<MethodBodyInfoType> options) {
        if (options.isEmpty()) {
            return AsmCodeFragment.NoOp.INSTANCE;
        }

        List<AsmCodeFragment> codeFragmentList = new ArrayList<>();

        if (options.contains(MethodBodyInfoType.ENTER)) {
            AsmCodeFragment segment = new AsmCodeFragment() {
                @Override
                public void implement(MethodVisitor mv, String currentType,
                                      int methodAccess, String methodName, String methodDesc,
                                      String signature, String[] exceptions) {

                }
            };
            codeFragmentList.add(segment);
        }

        return AsmCodeFragment.of(codeFragmentList);
    }

    static AsmCodeFragment of(@NotNull AsmCodeFragment... codeFragments) {
        List<AsmCodeFragment> codeFragmentList = Arrays.asList(codeFragments);
        return new Compound(codeFragmentList);
    }

    static AsmCodeFragment of(@NotNull List<AsmCodeFragment> codeFragmentList) {
        return new Compound(codeFragmentList);
    }

    enum NoOp implements AsmCodeFragment {
        INSTANCE;

        @Override
        public void implement(MethodVisitor mv, String currentType,
                              int methodAccess, String methodName, String methodDesc,
                              String signature, String[] exceptions) {
            // empty
        }
    }

    class Compound implements AsmCodeFragment {
        private final List<AsmCodeFragment> codeFragmentList;
        private final Set<AsmCodeTag> tagSet = new HashSet<>();

        private Compound(@NotNull List<AsmCodeFragment> codeFragmentList) {
            this.codeFragmentList = codeFragmentList;

            for (AsmCodeFragment segment : codeFragmentList) {
                Set<AsmCodeTag> tags = segment.tags();
                tagSet.addAll(tags);
            }
        }

        @Override
        public Set<AsmCodeTag> tags() {
            return tagSet;
        }

        @Override
        public void implement(MethodVisitor mv, String currentType,
                              int methodAccess, String methodName, String methodDesc,
                              String signature, String[] exceptions) {
            int size = codeFragmentList.size();
            for (int i = 0; i < size; i++) {
                AsmCodeFragment codeSegment = codeFragmentList.get(i);
                if (codeSegment == null) {
                    continue;
                }

                codeSegment.implement(mv, currentType, methodAccess, methodName, methodDesc, signature, exceptions);
            }
        }
    }

    enum Print implements AsmCodeFragment {
        CURRENT_THREAD_INFO {
            @Override
            public Set<AsmCodeTag> tags() {
                return EnumSet.of(AsmCodeTag.PRINT, AsmCodeTag.THREAD, AsmCodeTag.COMPUTE_MAX);
            }

            @Override
            public void implement(MethodVisitor mv, String currentType,
                                  int methodAccess, String methodName, String methodDesc,
                                  String signature, String[] exceptions) {
                AsmInsnUtilsForThread.printCurrentThreadInfo(mv);
            }
        },
        ALL_THREAD_INFO {
            @Override
            public Set<AsmCodeTag> tags() {
                return EnumSet.of(AsmCodeTag.PRINT, AsmCodeTag.THREAD, AsmCodeTag.COMPUTE_FRAMES);
            }

            @Override
            public void implement(MethodVisitor mv, String currentType,
                                  int methodAccess, String methodName, String methodDesc,
                                  String signature, String[] exceptions) {
                AsmInsnUtilsForThread.printAllThreadsInfo(mv, methodAccess, methodDesc);
            }
        };
    }

}
