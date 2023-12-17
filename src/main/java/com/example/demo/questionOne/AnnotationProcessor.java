package com.example.demo.questionOne;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

@SupportedAnnotationTypes({"ClassDocumentation", "MethodDocumentation"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class AnnotationProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(ClassDocumentation.class)) {
            if (element.getKind().isClass()) {
                ClassDocumentation classDoc = element.getAnnotation(ClassDocumentation.class);
                processClassDocumentation(element, classDoc);
            }
        }

        for (Element element : roundEnv.getElementsAnnotatedWith(MethodDocumentation.class)) {
            if (element.getKind().isExecutable()) {
                MethodDocumentation methodDoc = element.getAnnotation(MethodDocumentation.class);
                processMethodDocumentation(element, methodDoc);
            }
        }

        return true;
    }

    private void processClassDocumentation(Element element, ClassDocumentation classDoc) {
        String className = element.getSimpleName().toString();
        String documentation = classDoc.value();

        if (documentation.isEmpty()) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING,
                    "Class '" + className + "' is missing documentation");
        }
    }

    private void processMethodDocumentation(Element element, MethodDocumentation methodDoc) {
        String methodName = element.getSimpleName().toString();
        String documentation = methodDoc.value();

        if (documentation.isEmpty()) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING,
                    "Method '" + methodName + "' is missing documentation");
        }
    }
}
