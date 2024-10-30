package lsieun.asm.insn.opcode;

/**
 * <pre>
 *                                                 ┌─── cst ──────┼─── push()
 *                                                 │
 *                                                 │                           ┌─── pop()
 *                                                 │                           │
 *                                                 │              ┌─── pop ────┼─── pop2()
 *                                                 │              │            │
 *                                                 │              │            └─── ext ──────┼─── popByMethodDesc()
 *                                                 │              │
 *                                                 │              │                           ┌─── dupX1()
 *                                                 │              │            ┌─── dup() ────┤
 *                                                 │              │            │              └─── dupX2()
 *                                                 ├─── only ─────┤            │
 *                                                 │              ├─── dup ────┤              ┌─── dup2X1()
 *                                                 │              │            ├─── dup2() ───┤
 *                                                 │              │            │              └─── dup2X2()
 *                                                 │              │            │
 *                                                 │              │            └─── ext ──────┼─── dupValueOnStack()
 *                                                 │              │
 *                                                 │              │            ┌─── swap()
 *                                                 │              └─── swap ───┤
 *                                                 │                           │              ┌─── swapUpAndDown()
 *                                                 │                           └─── ext ──────┤
 *                                                 │                                          └─── swapLeftAndRight()
 *                                                 │
 *                                                 │                                ┌─── wide/narrow ─────┼─── castPrimitive()
 *                                                 │                                │
 *                          ┌─── operand.stack ────┤              ┌─── primitive ───┤                                                          ┌─── box()
 *                          │                      │              │                 │                     ┌─── box ─────┼─── getBoxedType() ───┤
 *                          │                      │              │                 └─── ref:box/unbox ───┤                                    └─── valueOf()
 *                          │                      │              │                                       │
 *                          │                      │              │                                       └─── unbox ───┼─── unbox()
 *                          │                      │              │
 *                          │                      │              │                 ┌─── new ────┼─── newInstance()
 *                          │                      │              ├─── reference ───┤
 *                          │                      ├─── type ─────┤                 └─── cast ───┼─── checkCast()
 *                          │                      │              │
 *                          │                      │              │                                                       ┌─── newArrayRef()
 *                          │                      │              │                 ┌─── new ──────────┼─── newArray() ───┤
 *                          │                      │              │                 │                                     └─── newArrayPrimitive()
 *                          │                      │              │                 │
 *                          │                      │              │                 ├─── length ───────┼─── arrayLength()
 *                          │                      │              │                 │
 *                          │                      │              └─── array ───────┤                  ┌─── arrayLoad()
 *                          │                      │                                ├─── load/store ───┤
 *                          │                      │                                │                  └─── arrayStore()
 *                          │                      │                                │
 *                          │                      │                                │                  ┌─── arrayFromStackByMethodDesc()
 *                          │                      │                                └─── ext:method ───┤
 *                          │                      │                                                   └─── arrayToStackByMethodDesc()
 *                          │                      │
 *                          │                      │                             ┌─── static ────┼─── invokeStatic()
 *                          │                      │                             │
 *                          │                      └─── method ───┼─── invoke ───┼─── special ───┼─── invokeConstructor()
 * AsmInsnUtilsForOpcode ───┤                                                    │
 *                          │                                                    └─── virtual ───┼─── invokeVirtual()
 *                          │
 *                          │                                                       ┌─── loadInsn()
 *                          │                                         ┌─── insn ────┤
 *                          │                                         │             └─── storeInsn()
 *                          │                                         │
 *                          │                                         ├─── this ────┼─── loadThis()
 *                          │                                         │
 *                          │                                         │             ┌─── slotIndex ────┼─── getArgSlotIndex()
 *                          │                                         ├─── args ────┤
 *                          ├─── stack&lt;-&gt;local ────┼─── load/store ───┤             │                  ┌─── loadArg() ────┼─── loadArgs()
 *                          │                                         │             └─── load/store ───┤
 *                          │                                         │                                └─── storeArg()
 *                          │                                         │
 *                          │                                         │             ┌─── slotIndex ────┼─── getFirstLocalSlotIndex()
 *                          │                                         │             │
 *                          │                                         │             │                  ┌─── list ───┼─── getEmptyLocalTypes()
 *                          │                                         │             ├─── type ─────────┤
 *                          │                                         └─── local ───┤                  │            ┌─── getLocalType()
 *                          │                                                       │                  └─── one ────┤
 *                          │                                                       │                               └─── setLocalType()
 *                          │                                                       │
 *                          │                                                       │                  ┌─── loadLocal()
 *                          │                                                       └─── load/store ───┤
 *                          │                                                                          └─── storeLocal()
 *                          │
 *                          └─── local.variable ───┼─── iinc()
 * </pre>
 */
public class AsmInsnUtilsForOpcode {



}
