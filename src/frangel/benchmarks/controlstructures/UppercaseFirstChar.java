package frangel.benchmarks.controlstructures;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.TaskCreator;

public enum UppercaseFirstChar implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.CONTROL_STRUCTURES.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("uppercaseFirstChar")
                .setInputTypes(String.class)
                .setInputNames("str")
                .setOutputType(String.class)
                .addTags(Tag.IF);

        task.addExample(new Example()
                .setInputs(() -> new Object[] { "computer science is COOL" })
                .setOutput("Computer science is COOL"));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { "123abc" })
                .setOutput("123abc"));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { "aaa" })
                .setOutput("Aaa"));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { "a" })
                .setOutput("A"));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { "" })
                .setOutput(""));

        return task;
    }

    static String solution(String str) {
        if (str.isEmpty())
            return "";
        return str.toUpperCase().charAt(0) + str.substring(1);
    }
}
