package frangel;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;

import frangel.utils.Colors;

public class UnitTester {

    public static boolean test(SynthesisTask task, Class<?> cls, String methodName) {
        Class<?>[] inputTypes = task.getInputTypes();
        Method m = null;

        System.out.println("Testing " + methodName + " for task " + task.getName() + "...");

        task.finalizeSetup();

        try {
            m = cls.getDeclaredMethod(methodName, inputTypes);
        } catch (NoSuchMethodException | SecurityException e) {
            System.out.println("Error: Could not find method with signature "
                    + Colors.color(Colors.CYAN, getSignature(cls, methodName, inputTypes)));
            System.out.println(Colors.color(Colors.BRIGHT_RED, methodName + " FAILED."));
            return false;
        }
        if (!m.getReturnType().equals(task.getOutputType())) {
            System.out.println("Error: " + methodName + " returns "
                    + Colors.color(Colors.BRIGHT_RED, m.getReturnType().getCanonicalName())
                    + " (" + Colors.color(Colors.CYAN, task.getOutputType().getCanonicalName()) + " expected)");
            System.out.println(Colors.color(Colors.BRIGHT_RED, methodName + " FAILED."));
            return false;
        }
        m.setAccessible(true);
        if (!Modifier.isStatic(m.getModifiers())) {
            System.out.println("Error: " + methodName + " is "
                    + Colors.color(Colors.BRIGHT_RED, "non-static") + " (must be " + Colors.color(Colors.CYAN, "static") + ")");
            System.out.println(Colors.color(Colors.BRIGHT_RED, methodName + " FAILED."));
            return false;
        }

        int numPassed = 0;
        List<Example> examples = task.getExamples();
        for (int ei = 0; ei < examples.size(); ei++) {
            System.out.print("\n");
            Example example = examples.get(ei);
            String exampleName = example.getNameWithIndex();

            Object[] inputs = example.getInputs();
            Object[] origInputs = example.getInputs();
            Object output;
            try {
                output = m.invoke(null, inputs);
            } catch (Exception e) {
                System.out.println(Colors.color(Colors.BRIGHT_RED, "  * " + exampleName + " failed with exception:"));
                e.printStackTrace();
                continue;
            }

            boolean pass = true;

            if (!example.checkOutput(output)) {
                pass = false;
                printExampleFailure(task, exampleName, origInputs);
                System.out.println();
                System.out.println("      - Got output: " +
                        Colors.color(Colors.BRIGHT_RED, task.objectToString(output)));
                if (example.hasOutputChecker())
                    System.out.println("        Rejected by output checker.");
                else
                    System.out.println("        Expected:   " + Colors.color(Colors.CYAN, task.objectToString(example.getOutput())));
            }

            for (int i = 0; i < inputs.length; i++) {
                if (!example.checkModifiedInput(i, inputs[i])) {
                    if (pass)
                        printExampleFailure(task, exampleName, origInputs);
                    pass = false;
                    System.out.println();
                    System.out.println("      - Modified " + task.getInputName(i) + ": "
                            + Colors.color(Colors.BRIGHT_RED, task.objectToString(inputs[i])));
                    boolean rejectedByChecker = false;
                    Object expected = null;
                    if (!task.inputsMutable() && !example.hasModifiedInput(i+1))
                        expected = origInputs[i];
                    else if (example.hasModifiedInputChecker(i+1))
                        rejectedByChecker = true;
                    else
                        expected = example.getModifiedInput(i+1);
                    if (rejectedByChecker)
                        System.out.println("        Rejected by modified input checker.");
                    else
                        System.out.println("        Expected:  " + String.join("", Collections.nCopies(task.getInputName(i).length(), " ")) +
                                Colors.color(Colors.CYAN, task.objectToString(expected)));
                }
            }

            if (pass) {
                numPassed++;
                System.out.println(Colors.color(Colors.GREEN, "  * " + exampleName + " passed."));
            }
        }
        boolean result = numPassed == examples.size();
        System.out.println("\n" + Colors.color(result ? Colors.GREEN : Colors.BRIGHT_RED, methodName + " " + (result ? "PASSED" : "FAILED") + ": "
                + numPassed + " passed, " + (examples.size() - numPassed) + " failed.\n"));
        return result;
    }

    private static String getSignature(Class<?> cls, String methodName, Class<?>[] inputTypes) {
        String args = "";
        String sep = "";
        for (Class<?> c : inputTypes) {
            args += sep + c.getCanonicalName();
            sep = ", ";
        }
        return cls.getCanonicalName() + "." + methodName + "(" + args + ")";
    }

    private static void printExampleFailure(SynthesisTask task, String exampleName, Object[] origInputs) {
        System.out.println(Colors.color(Colors.BRIGHT_RED, "  * " + exampleName + " failed."));
        for (int i = 0; i < origInputs.length; i++)
            System.out.println("      - " + task.getInputName(i) + ": " + task.objectToString(origInputs[i]));
    }
}
