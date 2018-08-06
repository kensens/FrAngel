package frangel.benchmarks.geometry;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.TaskCreator;

public enum InscribedCircle implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.GEOMETRY.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("inscribedCircle")
                .setInputTypes(Rectangle2D.class)
                .setInputNames("rect")
                .setOutputType(Ellipse2D.Double.class)
                .makeInputsImmutable()
                .addClasses(Math.class)
                .addTags(/* none applicable */);

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Rectangle2D.Double(3.4, 5.6, 7.82, 7.81) })
                .setOutput(new Ellipse2D.Double(3.4, 5.6, 7.81, 7.81)));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Rectangle2D.Double(3.4, 5.6, 7.81, 7.82) })
                .setOutput(new Ellipse2D.Double(3.4, 5.6, 7.81, 7.81)));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Rectangle2D.Double(0, 5.6, 7.82, 7.81) })
                .setOutput(new Ellipse2D.Double(0, 5.6, 7.81, 7.81)));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Rectangle2D.Double(3.4, 0, 7.81, 7.82) })
                .setOutput(new Ellipse2D.Double(3.4, 0, 7.81, 7.81)));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Rectangle2D.Double(1, 0, 12.3, 45.6) })
                .setOutput(new Ellipse2D.Double(1, 0, 12.3, 12.3)));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Rectangle2D.Double(0, 1, 78.9, 12.3) })
                .setOutput(new Ellipse2D.Double(0, 1, 12.3, 12.3)));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Rectangle2D.Double(0, 1, 2, 1) })
                .setOutput(new Ellipse2D.Double(0, 1, 1, 1)));

        return task;
    }

    static Ellipse2D.Double solution(Rectangle2D rect) {
        // Assume positive width and height
        double diam = Math.min(rect.getWidth(), rect.getHeight());
        return new Ellipse2D.Double(rect.getX(), rect.getY(), diam, diam);
    }
}
