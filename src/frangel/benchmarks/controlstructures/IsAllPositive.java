package frangel.benchmarks.controlstructures;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.TaskCreator;

public enum IsAllPositive implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.CONTROL_STRUCTURES.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("isAllPositive")
                .setInputTypes(double[].class)
                .setInputNames("arr")
                .setOutputType(boolean.class)
                .addTags(Tag.FOREACH, Tag.IF);

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new double[] {0.1, 0.2, 12.3, 0.0001, 123.4, 1.2, 2.3} })
                .setOutput(true));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new double[] {0.1, 0.2, 12.3, -0.0001, 123.4, 1.2, 2.3} })
                .setOutput(false));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new double[] {0.01} })
                .setOutput(true));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new double[] {0.0} })
                .setOutput(false));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new double[] {-0.01} })
                .setOutput(false));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new double[] {} })
                .setOutput(true));

        return task;
    }

    static boolean solution(double[] arr) {
        for (double d : arr)
            if (d <= 0.0)
                return false;
        return true;
    }
}
