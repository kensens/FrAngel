package frangel.benchmarks.controlstructures;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.TaskCreator;

public enum IsIncreasing implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.CONTROL_STRUCTURES.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("isIncreasing")
                .setInputTypes(double[].class)
                .setInputNames("arr")
                .setOutputType(boolean.class)
                .makeInputsImmutable()
                .addTags(Tag.FOR, Tag.IF);

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new double[] {67, 67.8, 67.89, 67.891, 70, 71} })
                .setOutput(true));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new double[] {67, 67.8, 67.89, 67.891, 71, 71} })
                .setOutput(false));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new double[] {67, 67.8, 67.89, 73, 70, 71} })
                .setOutput(false));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new double[] {67, 67.8, 67.89, 67.89, 70, 71} })
                .setOutput(false));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new double[] {-68, -67.8, -67} })
                .setOutput(true));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new double[] {-68, -68.1, -67} })
                .setOutput(false));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new double[] {67.8, 67.9} })
                .setOutput(true));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new double[] {67.8, 67.3} })
                .setOutput(false));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new double[] {67.8} })
                .setOutput(true));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new double[] {} })
                .setOutput(true));

        return task;
    }

    static boolean solution(double[] arr) {
        for (int i = 0; i < arr.length - 1; i++)
            if (arr[i] >= arr[i+1])
                return false;
        return true;
    }
}
