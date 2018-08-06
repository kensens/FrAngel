package frangel.benchmarks.sypet;

import java.awt.geom.*;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.TaskCreator;

public enum SyPet_13_rotate implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.SYPET.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("sypet_13_rotate")
                .setInputTypes(Area.class, Point2D.class, double.class)
                .setInputNames("area", "point", "angle")
                .setOutputType(Area.class)
                .addPackages("java.awt.geom")
                .addEqualityTester(Area.class, (Area a1, Area a2) -> a1.equals(a2))
                .addTags(Tag.SINGLE_LINE); // Easily written in one line

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Area(new Rectangle2D.Double(0, 0, 10, 2)), new Point2D.Double(0, 0), Math.PI / 2 })
                .setOutput(new Area(new Rectangle2D.Double(-2, 0, 2, 10))));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Area(new Rectangle2D.Double(10, 20, 10, 2)), new Point2D.Double(10, 20), Math.PI / 2 })
                .setOutput(new Area(new Rectangle2D.Double(8, 20, 2, 10))));

        return task;
    }

    public static Area solution(Area arg0, Point2D arg1, double arg2) {
        double v1 = arg1.getX();
        double v2 = arg1.getY();
        AffineTransform v3 = AffineTransform.getRotateInstance(arg2, v1, v2);
        Area v4 = arg0.createTransformedArea(v3);
        return v4;
    }
}
