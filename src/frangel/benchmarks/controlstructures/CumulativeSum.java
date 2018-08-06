package frangel.benchmarks.controlstructures;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.TaskCreator;

public enum CumulativeSum implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.CONTROL_STRUCTURES.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("cumulativeSum")
                .setInputTypes(double[].class)
                .setInputNames("arr")
                .setOutputType(void.class)
                .addTags(Tag.FOR);

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new double[] {1.2, 3.4, -1.0, 100.0} })
                .setModifiedInput(1, new double[] {1.2, 4.6, 3.6, 103.6}));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new double[] {1.2, 3.4} })
                .setModifiedInput(1, new double[] {1.2, 4.6}));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new double[] {1.2, -1.2} })
                .setModifiedInput(1, new double[] {1.2, 0.0}));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new double[] {1.2, 1.2} })
                .setModifiedInput(1, new double[] {1.2, 2.4}));

        return task;
    }

    static void solution(double[] arr) {
        for (int i = 1; i < arr.length; i++)
            arr[i] += arr[i-1];
    }
}
