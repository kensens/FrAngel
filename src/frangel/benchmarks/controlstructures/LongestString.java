package frangel.benchmarks.controlstructures;

import java.util.List;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.BenchmarkUtils;
import frangel.benchmarks.TaskCreator;

public enum LongestString implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.CONTROL_STRUCTURES.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("longestString")
                .setInputTypes(List.class)
                .addGenerics(List.class, String.class)
                .setInputNames("list")
                .setOutputType(String.class)
                .makeInputsImmutable()
                .addTags(Tag.FOREACH, Tag.IF);

        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeList("long", "short", "", "longest", "longer") })
                .setOutput("longest"));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeList("long", "longest", "", "short", "longer") })
                .setOutput("longest"));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeList("123456", "abcde", "wxyz!") })
                .setOutput("123456"));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeList("12345", "abcdef", "wxyz!") })
                .setOutput("abcdef"));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeList("12345", "abcde", "wxyz!?") })
                .setOutput("wxyz!?"));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeList("ABC", "12345") })
                .setOutput("12345"));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeList("ABCDE", "123") })
                .setOutput("ABCDE"));

        return task;
    }

    static String solution(List<String> list) {
        String ans = "";
        for (String s : list)
            if (s.length() > ans.length())
                ans = s;
        return ans;
    }
}
