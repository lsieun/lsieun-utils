package lsieun.asm.utils;

import java.util.ArrayList;
import java.util.List;

// TODO: 迁移
public class StackFrameUtils {
    private static final List<String> locals = new ArrayList<>();
    private static final List<String> stack = new ArrayList<>();

    public static void addLocal(int index, boolean value) {
        add(index, value ? "1" : "0");
    }

    public static void addLocal(int index, byte value) {
        add(index, String.valueOf(value));
    }

    public static void addLocal(int index, char value) {
        add(index, String.valueOf(value));
    }

    public static void addLocal(int index, short value) {
        add(index, String.valueOf(value));
    }

    public static void addLocal(int index, int value) {
        add(index, String.valueOf(value));
    }

    public static void addLocal(int index, float value) {
        add(index, String.valueOf(value));
    }

    public static void addLocal(int index, long value) {
        add(index, String.valueOf(value));
        add(index + 1, "TOP");
    }

    public static void addLocal(int index, double value) {
        add(index, String.valueOf(value));
        add(index + 1, "TOP");
    }

    public static void addLocal(int index, Object value) {
        if (value == null) {
            add(index, "NULL");
        }
        else if (value instanceof String) {
            String str = (String) value;
            add(index, str);
        }
        else {
            String className = value.getClass().getName();
            int pos = className.lastIndexOf(".");
            String name = className.substring(pos + 1);
            add(index, name);
        }
    }

    public static void add(int index, String value) {
        checkLocals(index);
        locals.set(index, value);
    }

    public static void pushStack(boolean value) {
        push(value ? "1" : "0");
    }

    public static void pushStack(byte value) {
        push(String.valueOf(value));
    }

    public static void pushStack(char value) {
        push(String.valueOf(value));
    }

    public static void pushStack(short value) {
        push(String.valueOf(value));
    }

    public static void pushStack(int value) {
        push(String.valueOf(value));
    }

    public static void pushStack(float value) {
        push(String.valueOf(value));
    }

    public static void pushStack(long value) {
        push(String.valueOf(value));
        push("TOP");
    }

    public static void pushStack(double value) {
        push(String.valueOf(value));
        push("TOP");
    }

    public static void pushStack(Object value) {
        if (value == null) {
            push("NULL");
        }
        else if (value instanceof String) {
            String str = (String) value;
            push(str);
        }
        else {
            String className = value.getClass().getName();
            int pos = className.lastIndexOf(".");
            String name = className.substring(pos + 1);
            push(name);
        }
    }

    public static void push(String value) {
        stack.add(value);
    }

    public static void dup() {
        int size = stack.size();
        String item = stack.get(size - 1);
        stack.add(item);
    }

    public static void dup2() {
        int size = stack.size();
        String item1 = stack.get(size - 2);
        String item2 = stack.get(size - 1);
        stack.add(item1);
        stack.add(item2);
    }

    public static void dupx1() {
        int size = stack.size();
        String item = stack.get(size - 1);
        stack.add(size - 2, item);
    }

    public static void dupx2() {
        int size = stack.size();
        String item = stack.get(size - 1);
        stack.add(size - 3, item);
    }

    public static void dup2x1() {
        int size = stack.size();
        String item1 = stack.get(size - 1);
        String item2 = stack.get(size - 2);
        stack.add(size - 3, item1);
        stack.add(size - 3, item2);
    }

    public static void dup2x2() {
        int size = stack.size();
        String item1 = stack.get(size - 1);
        String item2 = stack.get(size - 2);
        stack.add(size - 4, item1);
        stack.add(size - 4, item2);
    }

    public static void swap() {
        int size = stack.size();
        String item1 = stack.get(size - 1);
        String item2 = stack.get(size - 2);
        stack.set(size - 2, item1);
        stack.set(size - 1, item2);
    }

    public static void pop(int num) {
        int size = stack.size();
        for (int i = 0; i < num; i++) {
            int index = size - i - 1;
            stack.remove(index);
        }
    }

    public static void checkLocals(int index) {
        int size = locals.size();
        if (size > index) return;

        int diff = index + 1 - size;
        for (int i = 0; i < diff; i++) {
            locals.add("TOP");
        }
    }

    public static void printStackFrame() {
        String locals_str = list2String(locals);
        String stack_str = list2String(stack);
        String line = String.format("%s %s", locals_str, stack_str);
        System.out.println(line);
    }

    public static String list2String(List<String> list) {
        int size = list.size();

        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < size - 1; i++) {
            String item = list.get(i);
            sb.append(item).append(", ");
        }

        if (size > 0) {
            sb.append(list.get(size - 1));
        }
        sb.append("]");

        return sb.toString();
    }
}
