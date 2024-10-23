package lsieun.asm.visitor.transformation.method;

import java.util.EnumSet;

public enum MethodBodyInfoType {
    ENTER,
    EXIT,
    PARAMETER_VALUES,
    RETURN_VALUE,
    THREAD_INFO,
    CLASSLOADER,
    STACK_TRACE,
    ;

    public static final EnumSet<MethodBodyInfoType> ALL = EnumSet.allOf(MethodBodyInfoType.class);

    public static final EnumSet<MethodBodyInfoType> STACK_TRACE_ONLY = EnumSet.of(
            ENTER, STACK_TRACE, EXIT
    );

    public static final EnumSet<MethodBodyInfoType> PARAM_STACK_TRACE = EnumSet.of(
            ENTER, PARAMETER_VALUES, STACK_TRACE, EXIT
    );

    public static final EnumSet<MethodBodyInfoType> PARAM_RETURN = EnumSet.of(
            ENTER, PARAMETER_VALUES, RETURN_VALUE, EXIT
    );

    public static final EnumSet<MethodBodyInfoType> PARAM_STACK_TRACE_RETURN = EnumSet.of(
            ENTER, PARAMETER_VALUES, STACK_TRACE, RETURN_VALUE, EXIT
    );
}
