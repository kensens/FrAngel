package frangel.benchmarks.controlstructures;

import java.util.Set;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.BenchmarkUtils;
import frangel.benchmarks.TaskCreator;

public enum ArraySimpleFilter implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.CONTROL_STRUCTURES.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("arraySimpleFilter")
                .setInputTypes(String[].class, Set.class)
                .addGenerics(Set.class, String.class)
                .setInputNames("arr", "filter")
                .setOutputType(void.class)
                .makeInputsImmutable()
                .addTags(Tag.FOR, Tag.IF);

        task.addExample(new Example()
                .setInputs(() -> new Object[] {
                        new String[] {"bad", "awesome", "terrible", "", "Rude", "badly", "rude", "rude.."},
                        BenchmarkUtils.makeSet("terrible", "bad", "rude")
                })
                .setModifiedInput(1, new String[] {"", "awesome", "", "", "Rude", "badly", "", "rude.."}));

        task.addExample(new Example()
                .setInputs(() -> new Object[] {
                        new String[] {"terribl", "bad"},
                        BenchmarkUtils.makeSet("terrible", "bad", "rude")
                })
                .setModifiedInput(1, new String[] {"terribl", ""}));

        task.addExample(new Example()
                .setInputs(() -> new Object[] {
                        new String[] {"bad", "BAD"},
                        BenchmarkUtils.makeSet("terrible", "bad", "rude")
                })
                .setModifiedInput(1, new String[] {"", "BAD"}));

        task.addExample(new Example()
                .setInputs(() -> new Object[] {
                        new String[] {"not-bad!", "rude "},
                        BenchmarkUtils.makeSet("terrible", "bad", "rude")
                })
                .setModifiedInput(1, new String[] {"not-bad!", "rude "}));

        return task;
    }

    static void solution(String[] arr, Set<String> filter) {
        for (int i = 0; i < arr.length; i++)
            if (filter.contains(arr[i]))
                arr[i] = "";
    }
}
