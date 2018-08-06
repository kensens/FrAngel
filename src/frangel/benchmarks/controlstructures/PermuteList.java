package frangel.benchmarks.controlstructures;

import java.util.ArrayList;
import java.util.List;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.BenchmarkUtils;
import frangel.benchmarks.TaskCreator;

public enum PermuteList implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.CONTROL_STRUCTURES.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("permuteList")
                .setInputTypes(List.class, int[].class)
                .addGenerics(List.class, String.class)
                .setInputNames("list", "permutation")
                .setOutputType(ArrayList.class)
                .makeInputsImmutable()
                .addTags(Tag.FOREACH);

        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeList("a", "b", "c", "d", "e"), new int[] {3, 2, 5, 4, 1} })
                .setOutput(BenchmarkUtils.makeList("c", "b", "e", "d", "a")));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeList("a", "b"), new int[] {2, 1} })
                .setOutput(BenchmarkUtils.makeList("b", "a")));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeList("a"), new int[] {1} })
                .setOutput(BenchmarkUtils.makeList("a")));

        return task;
    }

    static ArrayList<String> solution(List<String> list, int[] permutation) {
        ArrayList<String> ans = new ArrayList<>();
        for (int i : permutation)
            ans.add(list.get(i - 1));
        return ans;
    }
}
