package frangel.benchmarks.github;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.TaskCreator;

public enum GuavaSwap implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.GITHUB.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("guava_swap")
                .setInputTypes(Object[].class, int.class, int.class)
                .setInputNames("array", "i", "j")
                .setOutputType(void.class)
                .addTags(/* none applicable */);

        // no unit tests in guava-master/guava-tests/test/com/google/common/collect/ObjectArraysTest.java

        // added examples
        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Object[] {"abc", 123, null, false, "", 1.2, "?"}, 3, 5 })
                .setModifiedInput(1, new Object[] {"abc", 123, null, 1.2, "", false, "?"}));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Object[] {"abc", 123, null, false, "", 1.2, "?"}, 2, 5 })
                .setModifiedInput(1, new Object[] {"abc", 123, 1.2, false, "", null, "?"}));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Object[] {"abc", 123, null, false, "", 1.2, "?"}, 5, 2 })
                .setModifiedInput(1, new Object[] {"abc", 123, 1.2, false, "", null, "?"}));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Object[] {"abc", 123}, 0, 1 })
                .setModifiedInput(1, new Object[] {123, "abc"}));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Object[] {"abc", null}, 0, 1 })
                .setModifiedInput(1, new Object[] {null, "abc"}));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Object[] {null, "abc"}, 0, 1 })
                .setModifiedInput(1, new Object[] {"abc", null}));

        return task;
    }

    // from guava-master/guava/src/com/google/common/collect/ObjectArrays.java
    static void solution(Object[] array, int i, int j) {
        Object temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
}
