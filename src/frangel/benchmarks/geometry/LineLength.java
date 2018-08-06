package frangel.benchmarks.geometry;

import java.awt.geom.Line2D;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.BenchmarkUtils;
import frangel.benchmarks.TaskCreator;

public enum LineLength implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.GEOMETRY.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("lineLength")
                .setInputTypes(Line2D.class)
                .setInputNames("line")
                .setOutputType(double.class)
                .addPackages("java.awt.geom")
                .makeInputsImmutable()
                .addEqualityTester(Line2D.class, BenchmarkUtils::equalsLine2D)
                .addTags(Tag.SINGLE_LINE);

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Line2D.Double(3.0, 4.0, 3.6, -5.0) })
                .setOutput(Math.sqrt(81.36)));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Line2D.Double(0.0, 0.0, 60.0, -80.0) })
                .setOutput(100.0));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Line2D.Double(60.0, -80.0, 0.0, 0.0) })
                .setOutput(100.0));

        return task;
    }

    static double solution(Line2D line) {
        return line.getP1().distance(line.getP2());
    }
}
