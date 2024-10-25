package lsieun.archunit;

import lsieun.core.match.LogicAssistant;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaField;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;

import java.util.Set;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;

@AnalyzeClasses(packages = "lsieun", importOptions = {
        ImportOption.DoNotIncludeTests.class,
        ImportOption.DoNotIncludeJars.class
})
class MatchArcUnitTest {
    @ArchTest
    void matches_must_reside_in_a_match_package(JavaClasses classes) {
        // (1) rule
        ArchRule rule = classes()
                .that().haveNameMatching(".*Match")
                .and().haveNameNotMatching(".*[$].*")       // 排除内部类的干扰
                .and().haveSimpleNameStartingWith("Add")
                .should().resideInAPackage("..sam.chain..")
                .as("Matches should reside in a package '..match..'");

        // (2) check
        rule.check(classes);
    }

    @ArchTest
    void matches_should_be_suffixed(JavaClasses classes) {
        // (1) rule
        ArchRule rule = classes()
                .that().resideInAPackage("..sam.match..")
                .and().haveNameNotMatching(".*[$].*")       // 排除内部类的干扰
                .and().haveSimpleNameNotEndingWith("Buddy") // 排除 TextMatchBuddy
                .should().haveSimpleNameEndingWith("Match");

        // (2) check
        rule.check(classes);
    }

    @ArchTest
    void matches_should_have_a_logic_field(JavaClasses classes) {
        JavaClass logicClass = classes.get(LogicAssistant.class);

        ArchCondition<JavaClass> haveALogicField = new ArchCondition<>("have a logic field") {
            @Override
            public void check(JavaClass item, ConditionEvents events) {
                Set<JavaField> fields = item.getFields();
                boolean found = false;
                for (JavaField field : fields) {
                    String name = field.getName();
                    JavaClass rawType = field.getRawType();
                    if ("LOGIC".equals(name) && rawType.equals(logicClass)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    String message = String.format("Class %s does not have a 'LOGIC' field", item.getName());
                    events.add(SimpleConditionEvent.violated(item, message));
                }
            }
        };

        // (2)
        ArchRule rule = classes().that().resideInAnyPackage("..sam.match..")
                .and().haveSimpleNameEndingWith("Match")
                .and().areInterfaces()
                .and().areAnnotatedWith(FunctionalInterface.class)
                .should(haveALogicField);

        // (3)
        rule.check(classes);
    }

    @ArchTest
    void methods_in_match_should_return_bool(JavaClasses classes) {
        // (1) rule
        ArchRule rule = methods()
                .that().haveName("test")
                .and().doNotHaveModifier(JavaModifier.SYNTHETIC)
                .and().areDeclaredInClassesThat(
                        JavaClass.Predicates.resideInAnyPackage("..sam.match..").and(
                                JavaClass.Predicates.simpleNameEndingWith("Match")
                        ).and(
                                DescribedPredicate.not(JavaClass.Predicates.simpleNameContaining("$"))
                        )
                )
                .should().haveModifier(JavaModifier.ABSTRACT)
                .andShould().haveRawReturnType(boolean.class);

        // (2) check
        rule.check(classes);
    }
}
