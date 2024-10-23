package lsieun.archunit;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

public class SamTest {
    @Test
    void samTypeShouldHaveFunctionalInterface() {
        // (1) classes
        JavaClasses classes = new ClassFileImporter().importPackages("lsieun");

        // (2) rule
        ArchRule rule = classes()
                .that().resideInAPackage("..sam..")
                .and().haveSimpleNameStartingWith("Add")
                .should().beAnnotatedWith(FunctionalInterface.class);

        // (3) check
        rule.check(classes);
    }
}
