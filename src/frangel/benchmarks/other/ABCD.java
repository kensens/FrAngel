package frangel.benchmarks.other;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.TaskCreator;

public enum ABCD implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.OTHER.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("abcd")
                .setInputTypes(String.class, String.class, String.class, String.class)
                .setInputNames("a", "b", "c", "d")
                .setOutputType(String.class)
                .addTags(Tag.SINGLE_LINE);

        task.addExample(new Example()
                .setInputs(() -> new Object[] { "hi", " ", "bye", "!" })
                .setOutput("hi bye!"));

        return task;
    }

    static String solution(String a, String b, String c, String d) {
        return a + b + c + d;
    }
}
