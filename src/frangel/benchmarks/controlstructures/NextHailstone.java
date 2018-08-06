package frangel.benchmarks.controlstructures;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.TaskCreator;

public enum NextHailstone implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.CONTROL_STRUCTURES.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("nextHailstone")
                .setInputTypes(int.class)
                .setInputNames("num")
                .setOutputType(int.class)
                .addLiterals(int.class, 3)
                .addTags(Tag.IF);

        task.addExample(new Example()
                .setInputs(() -> new Object[] { 1 })
                .setOutput(1));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { 2 })
                .setOutput(1));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { 3 })
                .setOutput(10));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { 123456 })
                .setOutput(61728));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { 1234567 })
                .setOutput(3703702));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { 500000004 })
                .setOutput(250000002));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { 500000005 })
                .setOutput(1500000016));

        return task;
    }

    static int solution(int num) {
        if (num == 1)
            return 1;
        if (num % 2 == 0)
            return num / 2;
        else
            return 3 * num + 1;
    }
}
