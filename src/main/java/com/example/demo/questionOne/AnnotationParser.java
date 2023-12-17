package com.example.demo.questionOne;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@interface ClassDocumentation {
    String value() default "";
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@interface MethodDocumentation {
    String value() default "";
}

public class AnnotationParser {

    public static void main(String[] args) {
        parseClassDocumentation(MyClass.class);
        parseMethodDocumentation(MyClass.class);
    }

    private static void parseClassDocumentation(Class<?> clazz) {
        ClassDocumentation classDoc = clazz.getAnnotation(ClassDocumentation.class);
        if (classDoc != null) {
            System.out.println("Class Documentation: " + classDoc.value());
        } else {
            System.out.println("No Class Documentation");
        }
    }

    private static void parseMethodDocumentation(Class<?> clazz) {
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            MethodDocumentation methodDoc = method.getAnnotation(MethodDocumentation.class);
            if (methodDoc != null) {
                System.out.println("Method: " + method.getName() + ", Documentation: " + methodDoc.value());
            } else {
                System.out.println("Method: " + method.getName() + ", No Documentation");
            }
        }
    }
}
