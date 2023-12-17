package com.example.demo.questionOne;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DocumentationExtractor {
    private static final List<String> missingClassDocumentation = new ArrayList<>();
    private static final List<String> missingMethodDocumentation = new ArrayList<>();
    private static final List<String> extractedDocumentation = new ArrayList<>();

    public static void main(String[] args) {
        for (Class<?> clazz : getClassesWithAnnotation("com.example.demo.questionOne", ClassDocumentation.class)) {
            extractClassDocumentation(clazz);
        }

        for (Class<?> clazz : getClasses("com.example.demo.questionOne")) {
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent(MethodDocumentation.class)) {
                    extractMethodDocumentation(method);
                }
            }
        }

        generateReports();
        saveDocumentationToFile("documentation.txt", extractedDocumentation);
    }

    private static List<Class<?>> getClassesWithAnnotation(String packageName, Class<? extends Annotation> annotationClass) {
        List<Class<?>> result = new ArrayList<>();
        for (Class<?> clazz : getClasses(packageName)) {
            if (clazz.isAnnotationPresent(annotationClass)) {
                result.add(clazz);
            }
        }
        return result;
    }

    private static List<Class<?>> getClasses(String packageName) {
        List<Class<?>> classes = new ArrayList<>();
        String packagePath = packageName.replace('.', '/');
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL packageUrl = classLoader.getResource(packagePath);

        if (packageUrl != null) {
            File packageDirectory = new File(packageUrl.getFile());
            if (packageDirectory.exists() && packageDirectory.isDirectory()) {
                File[] files = packageDirectory.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.isFile() && file.getName().endsWith(".class")) {
                            String className = packageName + "." + file.getName().replace(".class", "");
                            try {
                                Class<?> clazz = Class.forName(className);
                                classes.add(clazz);
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }

        return classes;
    }

    private static void extractClassDocumentation(Class<?> clazz) {
        ClassDocumentation classDoc = clazz.getAnnotation(ClassDocumentation.class);
        if (classDoc == null || classDoc.value().isEmpty()) {
            missingClassDocumentation.add(clazz.getName());
        } else {
            extractedDocumentation.add("Class: " + clazz.getName() + "\n" + classDoc.value() + "\n");
        }
    }

    private static void extractMethodDocumentation(Method method) {
        MethodDocumentation methodDoc = method.getAnnotation(MethodDocumentation.class);
        if (methodDoc == null || methodDoc.value().isEmpty()) {
            missingMethodDocumentation.add(method.getDeclaringClass().getName() + "." + method.getName());
        } else {
            extractedDocumentation.add("Method: " + method.getDeclaringClass().getName() +
                    "." + method.getName() + "\n" + methodDoc.value() + "\n");
        }
    }

    private static void generateReports() {
        System.out.println("Classes with missing documentation: " + missingClassDocumentation);
        System.out.println("Methods with missing documentation: " + missingMethodDocumentation);
    }

    private static void saveDocumentationToFile(String fileName, List<String> documentation) {
        try (FileWriter writer = new FileWriter(fileName)) {
            for (String doc : documentation) {
                writer.write(doc);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}