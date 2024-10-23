package lsieun.asm.insn.desc;

import org.objectweb.asm.Type;

public class AsmInsnUtilsForDesc {
    public static String getDescForStringBuilderAppend(Type t) {
        int sort = t.getSort();
        String descriptor;
        if (sort == Type.SHORT) {
            descriptor = "(I)Ljava/lang/StringBuilder;";
        }
        else if (sort >= Type.BOOLEAN && sort <= Type.DOUBLE) {
            descriptor = "(" + t.getDescriptor() + ")Ljava/lang/StringBuilder;";
        }
        else {
            descriptor = "(Ljava/lang/Object;)Ljava/lang/StringBuilder;";
        }
        return descriptor;
    }
}
