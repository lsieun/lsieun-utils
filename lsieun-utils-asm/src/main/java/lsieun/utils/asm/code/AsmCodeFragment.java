package lsieun.utils.asm.code;

import jakarta.annotation.Nonnull;
import lsieun.utils.asm.insn.AsmInsnUtilsForCodeFragment;
import lsieun.utils.asm.visitor.transformation.modify.method.MethodBodyInfoType;
import org.objectweb.asm.MethodVisitor;

import java.util.*;

/**
 * <pre>
 *                    ┌─── NoOp
 *                    │
 *                    ├─── ForMax
 * AsmCodeFragment ───┤
 *                    ├─── ForFrame
 *                    │
 *                    │                ┌─── WithMax ─────┼─── CURRENT_THREAD_INFO
 *                    └─── Print ──────┤
 *                                     └─── WithFrame ───┼─── ALL_THREAD_INFO
 * </pre>
 */
public interface AsmCodeFragment {
    AsmCodeOptionForWrite getWriteType();

    default Set<AsmCodeTag> tags() {
        return EnumSet.noneOf(AsmCodeTag.class);
    }

    void implement(MethodVisitor mv, String currentType,
                   int methodAccess, String methodName, String methodDesc,
                   String signature, String[] exceptions);

    static AsmCodeFragment print(String str) {
        return new ForMax() {
            @Override
            public Set<AsmCodeTag> tags() {
                return EnumSet.of(AsmCodeTag.PRINT);
            }

            @Override
            public void implement(MethodVisitor mv, String currentType, int methodAccess, String methodName, String methodDesc, String signature, String[] exceptions) {
                AsmInsnUtilsForCodeFragment.printMessage(mv, str);
            }
        };
    }

    static AsmCodeFragment print(@Nonnull Set<MethodBodyInfoType> options) {
        if (options.isEmpty()) {
            return AsmCodeFragment.NoOp.INSTANCE;
        }

        List<AsmCodeFragment> codeFragmentList = new ArrayList<>();

        if (options.contains(MethodBodyInfoType.ENTER)) {
            ForMax segment = new ForMax() {
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

    static AsmCodeFragment of(@Nonnull AsmCodeFragment... codeFragments) {
        List<AsmCodeFragment> codeFragmentList = Arrays.asList(codeFragments);
        return new Compound(codeFragmentList);
    }

    static AsmCodeFragment of(@Nonnull List<AsmCodeFragment> codeFragmentList) {
        return new Compound(codeFragmentList);
    }

    enum NoOp implements AsmCodeFragment {
        INSTANCE;

        @Override
        public AsmCodeOptionForWrite getWriteType() {
            return AsmCodeOptionForWrite.NONE;
        }

        @Override
        public void implement(MethodVisitor mv, String currentType,
                              int methodAccess, String methodName, String methodDesc,
                              String signature, String[] exceptions) {
            // empty
        }
    }

    class Compound implements AsmCodeFragment {
        private final List<AsmCodeFragment> codeFragmentList;
        private final AsmCodeOptionForWrite writeType;
        private final Set<AsmCodeTag> tagSet = new HashSet<>();

        private Compound(@Nonnull List<AsmCodeFragment> codeFragmentList) {
            this.codeFragmentList = codeFragmentList;
            List<AsmCodeOptionForWrite> list = codeFragmentList.stream()
                    .map(AsmCodeFragment::getWriteType)
                    .toList();
            this.writeType = AsmCodeOptionForWrite.combine(list);
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
        public AsmCodeOptionForWrite getWriteType() {
            return writeType;
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

    @FunctionalInterface
    interface ForMax extends AsmCodeFragment {
        @Override
        default Set<AsmCodeTag> tags() {
            return EnumSet.of(AsmCodeTag.COMPUTE_MAX);
        }

        @Override
        default AsmCodeOptionForWrite getWriteType() {
            return AsmCodeOptionForWrite.COMPUTE_MAX;
        }
    }

    @FunctionalInterface
    interface ForFrame extends AsmCodeFragment {

        @Override
        default Set<AsmCodeTag> tags() {
            return EnumSet.of(AsmCodeTag.COMPUTE_FRAMES);
        }

        @Override
        default AsmCodeOptionForWrite getWriteType() {
            return AsmCodeOptionForWrite.COMPUTE_FRAME;
        }
    }

    interface Print {
        enum WithMax implements ForMax {
            CURRENT_THREAD_INFO {
                @Override
                public Set<AsmCodeTag> tags() {
                    return EnumSet.of(AsmCodeTag.PRINT, AsmCodeTag.THREAD, AsmCodeTag.COMPUTE_MAX);
                }

                @Override
                public void implement(MethodVisitor mv, String currentType,
                                      int methodAccess, String methodName, String methodDesc,
                                      String signature, String[] exceptions) {
                    AsmInsnUtilsForCodeFragment.printThreadInfo(mv);
                }
            };
        }

        enum WithFrame implements ForFrame {
            ALL_THREAD_INFO {
                @Override
                public Set<AsmCodeTag> tags() {
                    return EnumSet.of(AsmCodeTag.PRINT, AsmCodeTag.THREAD, AsmCodeTag.COMPUTE_FRAMES);
                }

                @Override
                public void implement(MethodVisitor mv, String currentType,
                                      int methodAccess, String methodName, String methodDesc,
                                      String signature, String[] exceptions) {
                    AsmInsnUtilsForCodeFragment.printAllThreadsInfo(mv, methodAccess, methodDesc);
                }
            };
        }
    }

}
