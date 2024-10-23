package lsieun.asm.sample;

import lsieun.asm.visitor.transformation.clazz.TypeAddToStringMethodVisitor;

@SuppressWarnings("all")
public class SampleForFieldToString {
    private static boolean boolValue = true;
    private String name;
    private int age;

    /**
     * @see TypeAddToStringMethodVisitor#visitEnd()
     */
    @Override
    public String toString() {
        return new StringBuilder()
                .append("lsieun.asm.sample.ToString@").append(System.identityHashCode(this)).append("{")
                .append("name = ").append(name)
                .append(", ").append("age = ").append(age)
                .append(", ").append("boolValue = ").append(boolValue)
                .append("}")
                .toString();
    }
}
