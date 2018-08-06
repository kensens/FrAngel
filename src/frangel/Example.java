package frangel;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

import frangel.utils.Utils;

public class Example {
    private String name;
    private String nameWithIndex;

    private Supplier<Object[]> inputSupplier;
    private Object output;
    private Predicate<Object> outputChecker;
    private Map<Integer, Object> modifiedInputs; // indices are 0-based internally, 1-based in console output and public API
    private Map<Integer, Predicate<Object>> modifiedInputCheckers;

    private SynthesisTask task; // Holds inputTypes, outputType, inputsMutable, equalityTesters

    private boolean outputGiven; // Force user to provide output (even if null) if outputType is not void

    private static Object[] emptyInputs() {
        return new Object[0];
    }

    public Example() {
        name = null;
        nameWithIndex = null;
        inputSupplier = Example::emptyInputs;
        output = null;
        outputChecker = null;
        modifiedInputs = new HashMap<>();
        modifiedInputCheckers = new HashMap<>();
        outputGiven = false;
    }

    public Example setName(String name) {
        this.name = name;
        return this;
    }

    public Example setInputs(Supplier<Object[]> inputSupplier) {
        this.inputSupplier = inputSupplier;
        return this;
    }

    public Example setOutput(Object output) {
        this.output = output;
        outputGiven = true;
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> Example setOutputChecker(Predicate<T> outputChecker) {
        this.outputChecker = x -> outputChecker.test((T) x);
        return this;
    }

    public Example setModifiedInput(int index, Object modifiedInput) {
        if (index <= 0)
            throw new SynthesisTaskException("Modified input index must be positive (1-based index)");
        modifiedInputs.put(index - 1, modifiedInput);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> Example setModifiedInputChecker(int index, Predicate<T> modifiedInputChecker) {
        if (index <= 0)
            throw new SynthesisTaskException("Modified input index must be positive (1-based index)");
        modifiedInputCheckers.put(index - 1, x -> modifiedInputChecker.test((T) x));
        return this;
    }

    public String getName() {
        return name;
    }

    public String getNameWithIndex() {
        return nameWithIndex;
    }

    public Object[] getInputs() {
        return inputSupplier.get();
    }

    public Object getOutput() {
        return output;
    }

    // Only called by SynthesisTask
    void setTask(SynthesisTask task, int index) {
        this.task = task;
        nameWithIndex = "Example " + (index + 1);
        if (name != null)
            nameWithIndex += " (" + name + ")";
    }

    void sanityCheck(int index) {
        String exampleName = getNameWithIndex();
        Object[] inputs = getInputs();
        if (inputs == null)
            throw new SynthesisTaskException("The input array is null");

        Class<?>[] inputTypes = task.getInputTypes();
        if (inputs.length != inputTypes.length) {
            throw new SynthesisTaskException(exampleName + " has " + inputs.length
                    + " input" + (inputs.length == 1 ? "" : "s") + " (" + inputTypes.length + " expected)");
        }
        for (int i = 0; i < inputs.length; i++) {
            if (!Utils.typeMatch(inputTypes[i], inputs[i]))
                throw new SynthesisTaskException(exampleName + " has input " + (i+1) + " of type "
                        + Utils.getTypeString(inputs[i]) + " (" + inputTypes[i].getCanonicalName() + " expected)");
        }

        if (!task.inputsMutable()) {
            Object[] inputs2 = getInputs();
            for (int i = 0; i < inputs.length; i++) {
                if (!modifiedInputs.containsKey(i) && !modifiedInputCheckers.containsKey(i)
                        && !checkObjectEquality(inputs[i], inputs2[i]))
                    throw new SynthesisTaskException("Cannot verify equality for two copies of " + exampleName + " input " + (i+1)
                            + " (you might need to provide a custom equality tester for type " + inputTypes[i].getCanonicalName() + ")");
            }
        }

        for (int i : modifiedInputs.keySet()) {
            if (i >= task.getNumInputs())
                throw new SynthesisTaskException(exampleName + " requires modifying input " + (i+1) + ", but there are only " + task.getNumInputs() + " inputs");
            if (!Utils.typeMatch(inputTypes[i], modifiedInputs.get(i)))
                throw new SynthesisTaskException(exampleName + " has modified input " + (i+1) + " of type "
                        + Utils.getTypeString(modifiedInputs.get(i)) + " (" + inputTypes[i].getCanonicalName() + " expected)");
        }
        for (int i : modifiedInputCheckers.keySet()) {
            if (i >= task.getNumInputs())
                throw new SynthesisTaskException(exampleName + " requires modifying input " + (i+1) + ", but there are only " + task.getNumInputs() + " inputs");
        }
        for (int i = 0; i < task.getNumInputs(); i++) {
            if (modifiedInputs.containsKey(i) && modifiedInputCheckers.containsKey(i))
                throw new SynthesisTaskException(exampleName + " has a modified input object and a modified input checker for input " + (i+1) + " (only one allowed)");
        }

        Class<?> outputType = task.getOutputType();
        if (!outputGiven && outputChecker == null && !outputType.equals(void.class))
            throw new SynthesisTaskException(exampleName + " has no output");
        if (outputType.equals(void.class) && outputChecker != null)
            throw new SynthesisTaskException(exampleName + " has an output checker, but no output is expected");
        if (outputGiven && !Utils.typeMatch(outputType, output))
            throw new SynthesisTaskException(exampleName + " has output of type "
                    + Utils.getTypeString(output) + " (" + outputType.getCanonicalName() + " expected)");
        if (outputGiven && outputChecker != null)
            throw new SynthesisTaskException(exampleName + " has an output object and an output checker (only one allowed)");
    }

    public boolean hasOutputChecker() {
        return outputChecker != null;
    }

    // 1-based index
    public boolean hasModifiedInputChecker(int index) {
        return modifiedInputCheckers.containsKey(index - 1);
    }

    // 1-based index
    public Object getModifiedInput(int index) {
        return modifiedInputs.get(index - 1);
    }

    public boolean hasModifiedInput(int index) {
        return modifiedInputs.containsKey(index - 1);
    }

    private boolean checkObjectEquality(Object expected, Object actual) {
        return Equals.customEquals(expected, actual, task.getEqualityTesters());
    }

    public boolean checkOutput(Object obj) {
        if (outputChecker != null)
            return outputChecker.test(obj);
        return checkObjectEquality(output, obj);
    }

    public boolean checkModifiedInputs(Object[] inputs) {
        for (int i = 0; i < inputs.length; i++)
            if (!checkModifiedInput(i, inputs[i]))
                return false;
        return true;
    }

    boolean checkModifiedInput(int index, Object input) { // 0-based index
        if (modifiedInputCheckers.containsKey(index))
            return modifiedInputCheckers.get(index).test(input);
        else if (modifiedInputs.containsKey(index))
            return checkObjectEquality(modifiedInputs.get(index), input);
        else if (!task.inputsMutable())
            return checkObjectEquality(getCachedOriginalInput(index), input);
        else
            return true;
    }

    private Object[] cachedOriginalInputs = null;
    private Object getCachedOriginalInput(int index) { // 0-based index
        if (cachedOriginalInputs == null)
            cachedOriginalInputs = inputSupplier.get();
        return cachedOriginalInputs[index];
    }
}
