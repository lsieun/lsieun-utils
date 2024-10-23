package lsieun.archunit;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "lsieun.asm")
class AsmInsnArchUnitTest {
    @ArchTest
    void services_should_not_access_controllers(JavaClasses importedClasses) {
        // (1) classes
        JavaClasses classes = importedClasses;

        // (2) rule
        ArchRule rule = noClasses()
                .that().resideInAPackage("..insn.opcode..")
                .should().accessClassesThat()
                .resideInAnyPackage(
                        "..insn.code..",
                        "..insn.method..",
                        "..insn.desc..",
                        "..insn.field..");

        // (3) check
        rule.check(classes);
    }
}
