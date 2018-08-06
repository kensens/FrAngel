package frangel.benchmarks.controlstructures;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.TaskCreator;

public enum LargestOddFactor implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.CONTROL_STRUCTURES.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("largestOddFactor")
                .setInputTypes(int.class)
                .setInputNames("num")
                .setOutputType(int.class)
                .addTags(Tag.WHILE);

        task.addExample(new Example()
                .setInputs(() -> new Object[] { 103 * 1024 })
                .setOutput(103));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { -221 * 2 })
                .setOutput(-221));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { 229 })
                .setOutput(229));

        return task;
    }

    static int solution(int num) {
        while (num % 2 == 0)
            num /= 2;
        return num;
    }
}
