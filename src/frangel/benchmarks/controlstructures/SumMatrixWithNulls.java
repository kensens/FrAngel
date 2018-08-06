package frangel.benchmarks.controlstructures;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.TaskCreator;

public enum SumMatrixWithNulls implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.CONTROL_STRUCTURES.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("sumMatrixWithNulls")
                .setInputTypes(double[][].class)
                .setInputNames("mat")
                .setOutputType(double.class)
                .makeInputsImmutable()
                .addTags(Tag.FOREACH, Tag.IF);

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new double[][] {{1.2, -3.4}, null, {10.0, 300.0, 5000.0}} })
                .setOutput(5307.8));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new double[][] {{1.2, -3.4}, {}, {10.0, 300.0, 5000.0}} })
                .setOutput(5307.8));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new double[][] {{1.2, -3.4}} })
                .setOutput(-2.2));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new double[][] {{1.2}} })
                .setOutput(1.2));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new double[][] {{}, {}} })
                .setOutput(0.0));

        return task;
    }

    static double solution(double[][] mat) {
        double ans = 0.0;
        for (double[] arr : mat)
            if (arr != null)
                for (double d : arr)
                    ans += d;
        return ans;
    }
}
