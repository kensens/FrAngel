package frangel.benchmarks.sypet;

import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.TaskCreator;

public enum SyPet_15_getIntersection implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.SYPET.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("sypet_15_getIntersection")
                .setInputTypes(Rectangle2D.class, Ellipse2D.class)
                .setInputNames("rect", "ellipse")
                .setOutputType(Rectangle2D.class)
                .addPackages("java.awt.geom")
                .addTags(Tag.SINGLE_LINE); // Easily written in one line

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Rectangle2D.Double(10, 20, 10, 2), new Ellipse2D.Double(9, 19, 2, 2) })
                .setOutput(new Rectangle2D.Double(10, 20, 1, 1)));

        return task;
    }

    public static Rectangle2D solution(Rectangle2D arg0, Ellipse2D arg1) {
        Area v1 = new Area(arg1);
        Rectangle2D v2 = v1.getBounds2D();
        Rectangle2D v3 = v2.createIntersection(arg0);
        return v3;
    }
}
