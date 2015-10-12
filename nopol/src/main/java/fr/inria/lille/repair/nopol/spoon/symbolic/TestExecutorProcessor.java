package fr.inria.lille.repair.nopol.spoon.symbolic;

import fr.inria.lille.commons.spoon.SpoonedFile;
import spoon.reflect.code.CtBlock;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class TestExecutorProcessor {

    public static void createMainTestClass(SpoonedFile spooner,
                                           String className) {
        Factory factory = spooner.spoonFactory();
        CtClass<Object> executeTestClass = factory.Class().create(className);

        CtTypeReference<String[]> typedReference = factory.Class()
                .createReference(String[].class);
        CtTypeReference<Object> returnTypedReference = factory.Class()
                .createReference("void");

        Set<ModifierKind> modifiers = new LinkedHashSet<>(2);
        modifiers.add(ModifierKind.PUBLIC);
        modifiers.add(ModifierKind.STATIC);

        HashSet<CtTypeReference<? extends Throwable>> exceptions = new HashSet<>();
        exceptions.add(factory.Class().createReference(Exception.class));

        CtBlock<?> body = spooner.spoonFactory().Core().createBlock();

        body.addStatement(factory
                .Code()
                .createCodeSnippetStatement(
                        "for (String method : methods) {\n\t\t"
                                + "String[] split = method.split(\"\\\\.\");\n\t\t"
                                + "Class.forName(method.replace(\".\" + split[split.length - 1], \"\")).getMethod(\"runJPFTest\", String[].class).invoke(null, new Object[] { new String[] { split[split.length - 1] }});}"));

        CtMethod<?> method = spooner
                .spoonFactory()
                .Method()
                .create(executeTestClass, modifiers, returnTypedReference,
                        "main", new ArrayList<CtParameter<?>>(), exceptions,
                        body);
        spooner.spoonFactory().Method()
                .createParameter(method, typedReference, "methods");
    }
}
