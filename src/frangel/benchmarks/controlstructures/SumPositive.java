package frangel.benchmarks.controlstructures;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.TaskCreator;

public enum SumPositive implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.CONTROL_STRUCTURES.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("sumPositive")
                .setInputTypes(double[].class)
                .setInputNames("arr")
                .setOutputType(double.class)
                .makeInputsImmutable()
                .addTags(Tag.FOREACH, Tag.IF);

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new double[] {1.2, -3.4, 10.0, -0.1, 0.1, 300.0} })
                .setOutput(311.3));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new double[] {1.2, 3.4} })
                .setOutput(4.6));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new double[] {1.2, -3.4} })
                .setOutput(1.2));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new double[] {-1.2, 3.4} })
                .setOutput(3.4));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new double[] {-1.2, -3.4} })
                .setOutput(0.0));

        return task;
    }

    static double solution(double[] arr) {
        double ans = 0.0;
        for (double d : arr)
            if (d > 0.0)
                ans += d;
        return ans;
    }
}
