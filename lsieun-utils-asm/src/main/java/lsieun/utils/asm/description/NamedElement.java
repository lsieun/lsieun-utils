package lsieun.utils.asm.description;

public interface NamedElement {
    String getName();

    interface WithDescriptor extends NamedElement {
        String getDescriptor();
    }

    interface WithRuntimeName extends NamedElement {
        String getInternalName();
    }
}
