package frangel.benchmarks.controlstructures;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.TaskCreator;

public enum CountNegative implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.CONTROL_STRUCTURES.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("countNegative")
                .setInputTypes(double[].class)
                .setInputNames("arr")
                .setOutputType(int.class)
                .makeInputsImmutable()
                .addTags(Tag.FOREACH, Tag.IF);

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new double[] {-0.1, -3.0, 1.2, -1.8, 0.0, -0.4, -0.01, -4.8, -2.0} })
                .setOutput(7));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new double[] {-0.1, -3.0, 1.2, -1.8, 0.0, -0.4, 0.01, -4.8, -2.0} })
                .setOutput(6));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new double[] {-0.1, -3.0, 1.2, -1.8, 0.01} })
                .setOutput(3));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new double[] {-0.01, -0.001} })
                .setOutput(2));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new double[] {0.01, -0.001} })
                .setOutput(1));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new double[] {-0.01, 0.001} })
                .setOutput(1));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new double[] {0.01, 0.001} })
                .setOutput(0));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new double[] {123.4} })
                .setOutput(0));

        return task;
    }

    static int solution(double[] arr) {
        int ans = 0;
        for (double d : arr)
            if (d < 0.0)
                ans++;
        return ans;
    }
}
