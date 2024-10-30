package lsieun.asm.cst;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Method;

public interface MyAsmConst {
    int ASM_API_VERSION = Opcodes.ASM9;


    String CLASS_DESCRIPTOR = "Ljava/lang/Class;";

    interface RefType {
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
    }

    interface ArrayType {
        Type OBJECT_ARRAY_TYPE = Type.getType("[Ljava/lang/Object;");
    }

    interface Box {
        Method BOOLEAN_VALUE = Method.getMethod("boolean booleanValue()");

        Method CHAR_VALUE = Method.getMethod("char charValue()");

        Method INT_VALUE = Method.getMethod("int intValue()");

        Method FLOAT_VALUE = Method.getMethod("float floatValue()");

        Method LONG_VALUE = Method.getMethod("long longValue()");

        Method DOUBLE_VALUE = Method.getMethod("double doubleValue()");
    }


    interface MethodNameAndDescConst {
        String NONE_TO_VOID_METHOD_DESC = "()V";

        /**
         * The internal name of a Java constructor.
         */
        String INIT_METHOD_NAME = "<init>";
        String INIT_METHOD_DEFAULT_DESC = NONE_TO_VOID_METHOD_DESC;

        /**
         * The internal name of a Java static initializer.
         */
        String CLINIT_METHOD_NAME = "<clinit>";
        String CLINIT_METHOD_DESC = NONE_TO_VOID_METHOD_DESC;

        // COMMON
        String HASH_CODE_METHOD_NAME = "hashCode";
        String HASH_CODE_METHOD_DESC = "()I";
        String EQUALS_METHOD_NAME = "equals";
        String EQUALS_METHOD_DESC = "(Ljava/lang/Object;)Z";
        String CLONE_METHOD_NAME = "clone";
        String CLONE_METHOD_DESC = "()Ljava/lang/Object;";
        String TO_STRING_METHOD_NAME = "toString";
        String TO_STRING_METHOD_DESC = "()Ljava/lang/String;";

        // Print
        String PRINT_STACK_FRAME_METHOD_NAME = "print$StackWalker$StackFrame";
        String PRINT_STACK_FRAME_METHOD_DESC = "(Ljava/lang/StackWalker$StackFrame;)V";
    }

    interface StringBuilderConst {
        String STRING_BUILDER_INTERNAL_NAME = "java/lang/StringBuilder";
        String APPEND_METHOD_NAME = "append";
        String APPEND_STRING_METHOD_DESC = "(Ljava/lang/String;)Ljava/lang/StringBuilder;";
    }
}
