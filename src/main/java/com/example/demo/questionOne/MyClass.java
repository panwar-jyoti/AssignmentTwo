package com.example.demo.questionOne;

// Example class with annotations and Javadoc comments
@ClassDocumentation("This is a sample class.")
public class MyClass {

    /**
     * This is a sample method.
     * @param param Sample parameter
     * @return Sample result
     */
    @MethodDocumentation("This method performs a sample operation.")
    public String myMethod(String param) {
        return "Result: " + param;
    }

    // Another method without MethodDocumentation annotation
    public void anotherMethod() {
        // Method without annotation and Javadoc
    }
}
