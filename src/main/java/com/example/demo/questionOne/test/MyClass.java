package com.example.demo.questionOne.test;

import com.example.demo.questionOne.ClassDocumentation;
import com.example.demo.questionOne.MethodDocumentation;

// Example class with annotations and Javadoc comments
@ClassDocumentation
public class MyClass {

    /**
     * This is a sample method.
     * @param param Sample parameter
     * @return Sample result
     */
    @MethodDocumentation
    public String myMethod(String param) {
        return "Result: " + param;
    }

    // Another method without MethodDocumentation annotation
    public void anotherMethod() {
        // Method without annotation and Javadoc
    }
}
