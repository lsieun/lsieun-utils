package lsieun.asm.common;

import lsieun.asm.visitor.analysis.check.ClassVisitorForCheckAnnotation;
import org.objectweb.asm.ClassReader;

import java.lang.annotation.Annotation;

public class ClassFileAnnotationUtils {
    public static boolean checkExists(byte[] bytes, Class<? extends Annotation> clazz) {
        ClassReader cr = new ClassReader(bytes);
        ClassVisitorForCheckAnnotation cv = new ClassVisitorForCheckAnnotation(clazz);
        cr.accept(cv, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
        return cv.exists();
    }
}
