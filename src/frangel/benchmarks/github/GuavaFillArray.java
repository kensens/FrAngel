package frangel.benchmarks.github;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.BenchmarkUtils;
import frangel.benchmarks.TaskCreator;

public enum GuavaFillArray implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.GITHUB.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("guava_fillArray")
                .setInputTypes(Iterable.class, Object[].class)
                .setInputNames("elements", "array")
                .setOutputType(Object[].class)
                .addTags(Tag.FOREACH);

        // no unit tests in guava-master/guava-tests/test/com/google/common/collect/ObjectArraysTest.java

        // added examples
        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeList("abc", true, null, 123, 12.3), new Object[7] })
                .setOutput(new Object[] { "abc", true, null, 123, 12.3, null, null }));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeList("abc", true), new Object[2] })
                .setOutput(new Object[] { "abc", true }));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeList("abc"), new Object[2] })
                .setOutput(new Object[] { "abc", null }));

        return task;
    }

    // from guava-master/guava/src/com/google/common/collect/ObjectArrays.java
    static Object[] solution(Iterable<?> elements, Object[] array) {
        int i = 0;
        for (Object element : elements) {
            array[i++] = element;
        }
        return array;
    }
}
