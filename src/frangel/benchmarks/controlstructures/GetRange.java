package frangel.benchmarks.controlstructures;

import java.util.ArrayList;
import java.util.List;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.BenchmarkUtils;
import frangel.benchmarks.TaskCreator;

public enum GetRange implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.CONTROL_STRUCTURES.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("getRange")
                .setInputTypes(int.class, int.class)
                .setInputNames("start", "end")
                .setOutputType(List.class)
                .addGenerics(List.class, Integer.class)
                .addClasses(ArrayList.class)
                .addTags(Tag.FOR);

        task.addExample(new Example()
                .setInputs(() -> new Object[] { 10, 15 })
                .setOutput(BenchmarkUtils.makeList(10, 11, 12, 13, 14)));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { 10, 11 })
                .setOutput(BenchmarkUtils.makeList(10)));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { 0, 1 })
                .setOutput(BenchmarkUtils.makeList(0)));

        return task;
    }

    static List<Integer> solution(int start, int end) {
        List<Integer> ans = new ArrayList<>();
        for (int i = start; i < end; i++)
            ans.add(i);
        return ans;
    }
}
