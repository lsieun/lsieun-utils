package lsieun.asm.description;

import org.objectweb.asm.Type;

import java.util.Objects;

public class TypeDesc implements ByteCodeElement, NamedElement.WithDescriptor {
    private final Type type;
    private final String name;
    private final String classname;
    private final String internalName;
    private final String descriptor;

    public TypeDesc(Type type) {
        Objects.requireNonNull(type, "type must not be null");
        this.type = type;
        classname = type.getClassName();
        name = classname.substring(classname.lastIndexOf('.') + 1);
        internalName = type.getInternalName();
        descriptor = type.getDescriptor();
    }

    @Override
    public ByteCodeElementType getElementType() {
        return ByteCodeElementType.TYPE;
    }

    @Override
    public String getDescriptor() {
        return descriptor;
    }

    @Override
    public String getName() {
        return name;
    }
}
