package frangel.benchmarks.controlstructures;

import java.util.ArrayList;
import java.util.regex.Pattern;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.BenchmarkUtils;
import frangel.benchmarks.TaskCreator;

public enum MatchingIndices implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.CONTROL_STRUCTURES.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("matchingIndices")
                .setInputTypes(String[].class, String.class)
                .setInputNames("strings", "pattern")
                .setOutputType(ArrayList.class)
                .addGenerics(ArrayList.class, Integer.class)
                .addPackages("java.util.regex")
                .makeInputsImmutable()
                .addTags(Tag.FOR, Tag.IF);

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new String[] {"aab", "aaab", "aaba", "abb", "abaab", "abab", "", "bab"}, "(a+b)*" })
                .setOutput(BenchmarkUtils.makeList(0, 1, 4, 5, 6)));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new String[] {"aab", "abab"}, "(a+b)*" })
                .setOutput(BenchmarkUtils.makeList(0, 1)));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new String[] {"bab", "abab"}, "(a+b)*" })
                .setOutput(BenchmarkUtils.makeList(1)));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new String[] {"abababaabab"}, "(a+b)*" })
                .setOutput(BenchmarkUtils.makeList(0)));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new String[] {"abababbaaab"}, "(a+b)*" })
                .setOutput(BenchmarkUtils.makeList()));

        return task;
    }

    static ArrayList<Integer> solution(String[] strings, String pattern) {
        ArrayList<Integer> ans = new ArrayList<>();
        for (int i = 0; i < strings.length; i++)
            if (Pattern.matches(pattern, strings[i]))
                ans.add(i);
        return ans;
    }
}
