package frangel.benchmarks.controlstructures;

import java.util.HashSet;
import java.util.Set;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.BenchmarkUtils;
import frangel.benchmarks.TaskCreator;

public enum ArrayUnion implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.CONTROL_STRUCTURES.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("arrayUnion")
                .setInputTypes(String[].class, String[].class)
                .setInputNames("arr1", "arr2")
                .setOutputType(Set.class)
                .addGenerics(Set.class, String.class)
                .addClasses(HashSet.class)
                .makeInputsImmutable()
                .addTags(Tag.FOREACH);

        task.addExample(new Example()
                .setInputs(() -> new Object[] {
                        new String[] {"abc", "", "hi", "1", "x"},
                        new String[] {"x!", "hi", "hi", "abc", "1", "def"},
                })
                .setOutput(BenchmarkUtils.makeSet("abc", "", "hi", "1", "x", "x!", "def")));

        task.addExample(new Example()
                .setInputs(() -> new Object[] {
                        new String[] {"abc", "x", "hi"},
                        new String[] {"hi", "x"},
                })
                .setOutput(BenchmarkUtils.makeSet("abc", "x", "hi")));

        task.addExample(new Example()
                .setInputs(() -> new Object[] {
                        new String[] {"hi"},
                        new String[] {"hi", "y", "abc"},
                })
                .setOutput(BenchmarkUtils.makeSet("hi", "y", "abc")));

        task.addExample(new Example()
                .setInputs(() -> new Object[] {
                        new String[] {},
                        new String[] {},
                })
                .setOutput(BenchmarkUtils.makeSet()));

        return task;
    }

    static Set<String> solution(String[] arr1, String[] arr2) {
        Set<String> ans = new HashSet<>();
        for (String s : arr1)
            ans.add(s);
        for (String s : arr2)
            ans.add(s);
        return ans;
    }
}
