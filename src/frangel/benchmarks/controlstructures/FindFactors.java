package frangel.benchmarks.controlstructures;

import java.util.HashSet;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.BenchmarkUtils;
import frangel.benchmarks.TaskCreator;

public enum FindFactors implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.CONTROL_STRUCTURES.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("findFactors")
                .setInputTypes(int.class)
                .setInputNames("num")
                .setOutputType(HashSet.class)
                .addGenerics(HashSet.class, Integer.class)
                .addTags(Tag.FOR, Tag.IF);

        task.addExample(new Example()
                .setInputs(() -> new Object[] { 18 })
                .setOutput(BenchmarkUtils.makeSet(1, 2, 3, 6, 9, 18)));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { 17 })
                .setOutput(BenchmarkUtils.makeSet(1, 17)));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { 9 })
                .setOutput(BenchmarkUtils.makeSet(1, 3, 9)));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { 6 })
                .setOutput(BenchmarkUtils.makeSet(1, 2, 3, 6)));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { 4 })
                .setOutput(BenchmarkUtils.makeSet(1, 2, 4)));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { 3 })
                .setOutput(BenchmarkUtils.makeSet(1, 3)));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { 1 })
                .setOutput(BenchmarkUtils.makeSet(1)));

        return task;
    }

    static HashSet<Integer> solution(int num) {
        HashSet<Integer> ans = new HashSet<>();
        for (int i = 1; i <= num; i++)
            if (num % i == 0)
                ans.add(i);
        return ans;
    }
}
