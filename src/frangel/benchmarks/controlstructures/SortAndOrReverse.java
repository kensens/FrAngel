package frangel.benchmarks.controlstructures;

import java.util.Collections;
import java.util.List;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.BenchmarkUtils;
import frangel.benchmarks.TaskCreator;

public enum SortAndOrReverse implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.CONTROL_STRUCTURES.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("sortAndOrReverse")
                .setInputTypes(List.class, boolean.class, boolean.class)
                .addGenerics(List.class, String.class)
                .setInputNames("list", "sort", "reverse")
                .setOutputType(void.class)
                .addClasses(Collections.class)
                .addTags(Tag.IF);

        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeList("b", "e", "a", "d", "c"), true, true})
                .setModifiedInput(1, BenchmarkUtils.makeList("e", "d", "c", "b", "a")));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeList("b", "e", "a", "d", "c"), true, false})
                .setModifiedInput(1, BenchmarkUtils.makeList("a", "b", "c", "d", "e")));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeList("b", "e", "a", "d", "c"), false, true})
                .setModifiedInput(1, BenchmarkUtils.makeList("c", "d", "a", "e", "b")));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeList("b", "e", "a", "d", "c"), false, false})
                .setModifiedInput(1, BenchmarkUtils.makeList("b", "e", "a", "d", "c")));

        return task;
    }

    static void solution(List<String> list, boolean sort, boolean reverse) {
        if (sort)
            Collections.sort(list);
        if (reverse)
            Collections.reverse(list);
    }
}
