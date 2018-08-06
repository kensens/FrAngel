package frangel.benchmarks.controlstructures;

import java.util.ArrayList;
import java.util.List;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.BenchmarkUtils;
import frangel.benchmarks.TaskCreator;

public enum FilterStringStart implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.CONTROL_STRUCTURES.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("filterStringStart")
                .setInputTypes(List.class, String.class)
                .addGenerics(List.class, String.class)
                .setInputNames("list", "start")
                .setOutputType(ArrayList.class)
                .makeInputsImmutable()
                .addTags(Tag.FOREACH, Tag.IF);

        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeList("start", "startswith", "xstart", "Start", "start 123", ""), "start" })
                .setOutput(BenchmarkUtils.makeList("start", "startswith", "start 123")));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeList("aaa", "aaAaaa"), "aa" })
                .setOutput(BenchmarkUtils.makeList("aaa", "aaAaaa")));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeList("aaa", "aAaaaa"), "aa" })
                .setOutput(BenchmarkUtils.makeList("aaa")));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeList("aba", "aaAaaa"), "aa" })
                .setOutput(BenchmarkUtils.makeList("aaAaaa")));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeList("aba", "aAaaaa"), "aa" })
                .setOutput(BenchmarkUtils.makeList()));

        return task;
    }

    static ArrayList<String> solution(List<String> list, String start) {
        ArrayList<String> ans = new ArrayList<>();
        for (String s : list)
            if (s.startsWith(start))
                ans.add(s);
        return ans;
    }
}
