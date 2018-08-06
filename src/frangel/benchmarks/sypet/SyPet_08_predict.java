package frangel.benchmarks.sypet;

import org.apache.commons.math.stat.regression.SimpleRegression;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.TaskCreator;

public enum SyPet_08_predict implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.SYPET.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("sypet_08_predict")
                .setInputTypes(double[][].class, double.class)
                .setInputNames("data", "value")
                .setOutputType(double.class)
                .addPackages("org.apache.commons.math.stat")
                .addTags(/* none applicable */); // CANNOT be written in one line because of addData()

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new double[][] {{1, 2}, {2, 3}, {3, 4}, {4, 5}, {5, 6}}, 1.5 })
                .setOutput(2.5));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new double[][] {{1, 3}, {2, 4}, {3, 5}, {4, 6}, {5, 7}}, 2.5 })
                .setOutput(4.5));

        return task;
    }

    public static double solution(double[][] arg0, double arg1) {
        SimpleRegression v1 = new SimpleRegression();
        v1.addData(arg0);
        double v2 = v1.predict(arg1);
        return v2;
    }
}
