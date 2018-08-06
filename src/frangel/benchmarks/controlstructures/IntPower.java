package frangel.benchmarks.controlstructures;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.TaskCreator;

public enum IntPower implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.CONTROL_STRUCTURES.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("intPower")
                .setInputTypes(int.class, int.class)
                .setInputNames("base", "exp")
                .setOutputType(int.class)
                .addTags(Tag.FOR);

        task.addExample(new Example()
                .setInputs(() -> new Object[] { 7, 11 })
                .setOutput(1977326743));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { 11, 4 })
                .setOutput(14641));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { -101, 3 })
                .setOutput(-1030301));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { -11111, 2 })
                .setOutput(123454321));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { 123, 1 })
                .setOutput(123));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { 123, 0 })
                .setOutput(1));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { 1, 10 })
                .setOutput(1));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { 0, 10 })
                .setOutput(0));

        return task;
    }

    static int solution(int base, int exp) {
        int ans = 1;
        for (int i = 0; i < exp; i++)
            ans *= base;
        return ans;
    }
}
