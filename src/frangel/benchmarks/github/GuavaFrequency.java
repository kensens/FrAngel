package frangel.benchmarks.github;

import java.util.Iterator;

import com.google.common.collect.Iterators;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.BenchmarkUtils;
import frangel.benchmarks.TaskCreator;

public enum GuavaFrequency implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.GITHUB.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("guava_frequency")
                .setInputTypes(Iterator.class, Object.class)
                .setInputNames("iterator", "element")
                .setOutputType(int.class)
                .addClasses(Iterators.class)
                .excludeMethods(
                        BenchmarkUtils.getMethod(Iterators.class, "frequency", Iterator.class, Object.class), // to synthesize
                        BenchmarkUtils.getMethod(Iterators.class, "cycle", Iterable.class), // cycle can lead to infinite loops
                        BenchmarkUtils.getMethod(Iterators.class, "cycle", Object[].class)) // cycle can lead to infinite loops
                .addTags(Tag.WHILE);

        // unit tests from guava-master/guava-tests/test/com/google/common/collect/IteratorsTest.java
        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeList("a", null, "b", null, "a", null).iterator(), "a" })
                .setOutput(2));
        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeList("a", null, "b", null, "a", null).iterator(), "b" })
                .setOutput(1));
        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeList("a", null, "b", null, "a", null).iterator(), "c" })
                .setOutput(0));
        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeList("a", null, "b", null, "a", null).iterator(), 4.2 })
                .setOutput(0));
        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeList("a", null, "b", null, "a", null).iterator(), null })
                .setOutput(3));

        // added examples
        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeList("", "A", "", "", "", "b", "", false, "", "").iterator(), "" })
                .setOutput(7));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeList(4, 3, 5, 3, 2, 3, 3, 4, 3).iterator(), 3 })
                .setOutput(5));

        return task;
    }

    // from guava-master/guava/src/com/google/common/collect/Iterators.java
    static int solution(Iterator<?> iterator, Object element) {
        int count = 0;
        while (Iterators.contains(iterator, element)) {
            count++;
        }
        return count;
    }
}
