package frangel.benchmarks.other;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.TaskCreator;

public enum AB implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.OTHER.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("ab")
                .setInputTypes(String.class, String.class)
                .setInputNames("a", "b")
                .setOutputType(String.class)
                .addTags(Tag.SINGLE_LINE);

        task.addExample(new Example()
                .setInputs(() -> new Object[] { "hi", "bye" })
                .setOutput("hibye"));

        return task;
    }

    static String solution(String a, String b) {
        return a + b;
    }
}
