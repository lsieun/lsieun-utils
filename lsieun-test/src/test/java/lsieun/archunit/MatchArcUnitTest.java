package lsieun.archunit;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

class MatchArcUnitTest {
    @Test
    void matches_must_reside_in_a_match_package() {
        // (1) classes
        JavaClasses classes = new ClassFileImporter().importPackages("lsieun");

        // (2) rule
        ArchRule rule = classes()
                .that().haveNameMatching(".*Match")
                .and().haveSimpleNameNotStartingWith("Add")
                .should().resideInAPackage("..match..")
                .as("Matches should reside in a package '..match..'");

        // (3) check
        rule.check(classes);
    }

    @Test
    void service_should_be_prefixed() {
        // (1) classes
        JavaClasses classes = new ClassFileImporter().importPackages("lsieun");

        // (2) rule
        ArchRule rule = classes()
                .that().resideInAPackage("..match")
                .should().haveSimpleNameEndingWith("Match");

        // (3) check
        rule.check(classes);
    }
}
