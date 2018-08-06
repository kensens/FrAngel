package frangel.benchmarks.github;

import java.util.Arrays;

import com.google.common.primitives.Ints;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.BenchmarkUtils;
import frangel.benchmarks.TaskCreator;

public enum GuavaSortDescending implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.GITHUB.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("guava_sortDescending")
                .setInputTypes(int[].class, int.class, int.class)
                .setInputNames("array", "fromIndex", "toIndex")
                .setOutputType(void.class)
                .addClasses(Arrays.class, Ints.class)
                .excludeMethods(
                        BenchmarkUtils.getMethod(Ints.class, "sortDescending", int[].class, int.class, int.class), // to synthesize
                        BenchmarkUtils.getMethod(Ints.class, "sortDescending", int[].class)) // alternate form
                .addTags(/* none applicable */);

        // unit tests from guava-master/guava-tests/test/com/google/common/primitives/IntsTest.java
        task.addExample(new Example()
                .setInputs(() -> new Object[] { new int[] {}, 0, 0 })
                .setModifiedInput(1, new int[] {}));
        task.addExample(new Example()
                .setInputs(() -> new Object[] { new int[] {1}, 0, 1 })
                .setModifiedInput(1, new int[] {1}));
        task.addExample(new Example()
                .setInputs(() -> new Object[] { new int[] {-1, -2, 1, 2}, 1, 3 })
                .setModifiedInput(1, new int[] {-1, 1, -2, 2}));
        // and others

        // added examples

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new int[] {1, 2, 3, 44, 55, 66, 777, 888}, 3, 7 })
                .setModifiedInput(1, new int[] {1, 2, 3, 777, 66, 55, 44, 888}));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new int[] {2, 888, 55, 1, 3, 777, 44, 66}, 4, 8 })
                .setModifiedInput(1, new int[] {2, 888, 55, 1, 777, 66, 44, 3}));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new int[] {2, 888, 55, 1, 3, 777, 44, 66}, 2, 7 })
                .setModifiedInput(1, new int[] {2, 888, 777, 55, 44, 3, 1, 66}));

        return task;
    }

    // from guava-master/guava/src/com/google/common/primitives/Ints.java
    static void solution(int[] array, int fromIndex, int toIndex) {
        //checkNotNull(array);
        //checkPositionIndexes(fromIndex, toIndex, array.length);
        Arrays.sort(array, fromIndex, toIndex);
        Ints.reverse(array, fromIndex, toIndex);
    }
}
