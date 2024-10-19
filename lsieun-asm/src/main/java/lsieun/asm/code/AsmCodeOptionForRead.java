package lsieun.asm.code;

import org.objectweb.asm.ClassReader;

/**
 * <pre>
 *                                ┌─── SKIP_CODE ───────┼─── method_info ───┼─── Code
 *                                │
 *                                │                                         ┌─── SourceFile
 *                                │                     ┌─── classfile ─────┤
 *                                │                     │                   └─── SourceDebugExtension
 *                                ├─── SKIP_DEBUG ──────┤
 *                                │                     │                   ┌─── MethodParameters
 *                                │                     │                   │
 * ClassReader::parsingOptions ───┤                     └─── method_info ───┤                        ┌─── LocalVariableTable
 *                                │                                         │                        │
 *                                │                                         └─── code ───────────────┼─── LocalVariableTypeTable
 *                                │                                                                  │
 *                                │                                                                  └─── LineNumberTable
 *                                │
 *                                ├─── SKIP_FRAMES ─────┼─── method_info ───┼─── code ───┼─── StackMapTable
 *                                │
 *                                └─── EXPAND_FRAMES ───┼─── method_info ───┼─── code ───┼─── StackMapTable
 * </pre>
 */
public enum AsmCodeOptionForRead {
    SKIP_CODE(ClassReader.SKIP_CODE),
    SKIP_DEBUG(ClassReader.SKIP_DEBUG),
    SKIP_FRAMES(ClassReader.SKIP_FRAMES),
    SKIP_DEBUG_FRAMES(ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES),
    EXPAND_FRAMES(ClassReader.EXPAND_FRAMES)
    ;
    public final int parsingOptions;

    AsmCodeOptionForRead(int parsingOptions) {
        this.parsingOptions = parsingOptions;
    }
}
