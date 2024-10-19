package lsieun.asm.visitor.analysis.check;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Type;

import java.lang.annotation.Annotation;

public class ClassVisitorForCheckAnnotation extends ClassVisitorForCheckExistense {
    private final Type annotationType;

    public ClassVisitorForCheckAnnotation(Class<? extends Annotation> clazz) {
        annotationType = Type.getType(clazz);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        if (descriptor.equals(annotationType.getDescriptor())) {
            existsFlag = true;
        }

        return super.visitAnnotation(descriptor, visible);
    }
}
