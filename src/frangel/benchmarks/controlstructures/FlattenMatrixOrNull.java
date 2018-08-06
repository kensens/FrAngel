package frangel.benchmarks.controlstructures;

import java.util.ArrayList;
import java.util.List;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.BenchmarkUtils;
import frangel.benchmarks.TaskCreator;

public enum FlattenMatrixOrNull implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.CONTROL_STRUCTURES.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("flattenMatrixOrNull")
                .setInputTypes(Object[][].class)
                .setInputNames("mat")
                .setOutputType(List.class)
                .addGenerics(List.class, Object.class)
                .addClasses(ArrayList.class)
                .makeInputsImmutable()
                .addTags(Tag.IF, Tag.FOREACH);

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Object[][] {
                    {1, 2, 3},
                    {},
                    {"a", null}
                } })
                .setOutput(BenchmarkUtils.makeList(1, 2, 3, "a", null)));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Object[][] {
                    {1, 2},
                } })
                .setOutput(BenchmarkUtils.makeList(1, 2)));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Object[][] {
                    {"!"}
                } })
                .setOutput(BenchmarkUtils.makeList("!")));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { null })
                .setOutput(BenchmarkUtils.makeList()));

        return task;
    }

    static List<Object> solution(Object[][] mat) {
        List<Object> ans = new ArrayList<>();
        if (mat != null)
            for (Object[] arr : mat)
                for (Object o : arr)
                    ans.add(o);
        return ans;
    }
}
