package lsieun.asm.cst;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Method;

public interface MyAsmConst {
    int ASM_API_VERSION = Opcodes.ASM9;

    /**
     * The internal name of a Java constructor.
     */
    String CONSTRUCTOR_INTERNAL_NAME = "<init>";

    /**
     * The internal name of a Java static initializer.
     */
    String TYPE_INITIALIZER_INTERNAL_NAME = "<clinit>";
    String PRINT_STACK_FRAME_METHOD_NAME = "print$StackWalker$StackFrame";
    String PRINT_STACK_FRAME_METHOD_DESC = "(Ljava/lang/StackWalker$StackFrame;)V";


    String CLASS_DESCRIPTOR = "Ljava/lang/Class;";

    Type BYTE_TYPE = Type.getObjectType("java/lang/Byte");

    Type BOOLEAN_TYPE = Type.getObjectType("java/lang/Boolean");

    Type SHORT_TYPE = Type.getObjectType("java/lang/Short");

    Type CHARACTER_TYPE = Type.getObjectType("java/lang/Character");

    Type INTEGER_TYPE = Type.getObjectType("java/lang/Integer");

    Type FLOAT_TYPE = Type.getObjectType("java/lang/Float");

    Type LONG_TYPE = Type.getObjectType("java/lang/Long");

    Type DOUBLE_TYPE = Type.getObjectType("java/lang/Double");

    Type NUMBER_TYPE = Type.getObjectType("java/lang/Number");

    Type OBJECT_TYPE = Type.getObjectType("java/lang/Object");

    Method BOOLEAN_VALUE = Method.getMethod("boolean booleanValue()");

    Method CHAR_VALUE = Method.getMethod("char charValue()");

    Method INT_VALUE = Method.getMethod("int intValue()");

    Method FLOAT_VALUE = Method.getMethod("float floatValue()");

    Method LONG_VALUE = Method.getMethod("long longValue()");

    Method DOUBLE_VALUE = Method.getMethod("double doubleValue()");
}
