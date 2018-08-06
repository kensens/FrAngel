package frangel.benchmarks.controlstructures;

import java.util.Set;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.BenchmarkUtils;
import frangel.benchmarks.TaskCreator;

public enum ArrayComplexFilter implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.CONTROL_STRUCTURES.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("arrayComplexFilter")
                .setInputTypes(String[].class, Set.class)
                .addGenerics(Set.class, String.class)
                .setInputNames("arr", "filter")
                .setOutputType(void.class)
                .makeInputsImmutable()
                .addTags(Tag.FOR, Tag.FOREACH, Tag.IF);

        task.addExample(new Example()
                .setInputs(() -> new Object[] {
                        new String[] {"Very bad service", "Awesome!", "", "terrible and rude...", "Food was great.", "not-bad :)"},
                        BenchmarkUtils.makeSet("terrible", "bad", "rude")
                })
                .setModifiedInput(1, new String[] {"", "Awesome!", "", "", "Food was great.", ""}));

        task.addExample(new Example()
                .setInputs(() -> new Object[] {
                        new String[] {"Very Bad service", "rude"},
                        BenchmarkUtils.makeSet("terrible", "Bad", "rude")
                })
                .setModifiedInput(1, new String[] {"", ""}));

        task.addExample(new Example()
                .setInputs(() -> new Object[] {
                        new String[] {"Very Bad service", "Very bad service"},
                        BenchmarkUtils.makeSet("terrible", "Bad", "rude")
                })
                .setModifiedInput(1, new String[] {"", "Very bad service"}));

        task.addExample(new Example()
                .setInputs(() -> new Object[] {
                        new String[] {"Greater", "rible"},
                        BenchmarkUtils.makeSet("terrible", "bad", "rude")
                })
                .setModifiedInput(1, new String[] {"Greater", "rible"}));

        return task;
    }

    static void solution(String[] arr, Set<String> filter) {
        for (int i = 0; i < arr.length; i++)
            for (String token : filter)
                if (arr[i].contains(token))
                    arr[i] = "";
    }
}
