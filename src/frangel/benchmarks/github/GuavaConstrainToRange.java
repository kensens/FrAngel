package frangel.benchmarks.github;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.TaskCreator;

public enum GuavaConstrainToRange implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.GITHUB.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("guava_constrainToRange")
                .setInputTypes(int.class, int.class, int.class)
                .setInputNames("value", "min", "max")
                .setOutputType(int.class)
                .addClasses(Math.class)
                .addTags(Tag.SINGLE_LINE);

        // unit tests from guava-master/guava-tests/test/com/google/common/primitives/IntsTest.java
        task.addExample(new Example()
                .setInputs(() -> new Object[] { 1, 0, 5 })
                .setOutput(1));
        task.addExample(new Example()
                .setInputs(() -> new Object[] { 1, 1, 5 })
                .setOutput(1));
        task.addExample(new Example()
                .setInputs(() -> new Object[] { 1, 3, 5 })
                .setOutput(3));
        task.addExample(new Example()
                .setInputs(() -> new Object[] { 0, -5, -1 })
                .setOutput(-1));
        task.addExample(new Example()
                .setInputs(() -> new Object[] { 5, 2, 2 })
                .setOutput(2));

        // added examples
        task.addExample(new Example()
                .setInputs(() -> new Object[] { 123, 67, 89 })
                .setOutput(89));
        task.addExample(new Example()
                .setInputs(() -> new Object[] { 45, 67, 89 })
                .setOutput(67));
        task.addExample(new Example()
                .setInputs(() -> new Object[] { 75, 67, 89 })
                .setOutput(75));

        return task;
    }

    // from guava-master/guava/src/com/google/common/primitives/Ints.java
    static int solution(int value, int min, int max) {
        //checkArgument(min <= max, "min (%s) must be less than or equal to max (%s)", min, max);
        return Math.min(Math.max(value, min), max);
    }
}
