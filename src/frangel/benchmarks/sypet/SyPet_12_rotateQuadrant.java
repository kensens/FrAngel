package frangel.benchmarks.sypet;

import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.TaskCreator;

public enum SyPet_12_rotateQuadrant implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.SYPET.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("sypet_12_rotateQuadrant")
                .setInputTypes(Rectangle2D.class, int.class)
                .setInputNames("rect", "num")
                .setOutputType(Rectangle2D.class)
                .addPackages("java.awt.geom")
                .addTags(Tag.SINGLE_LINE); // Easily written in one line

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Rectangle2D.Double(10, 20, 10, 2), 1 })
                .setOutput(new Rectangle2D.Double(-22, 10, 2, 10)));

        return task;
    }

    public static Rectangle2D solution(Rectangle2D arg0, int arg1) {
        Area v1 = new Area(arg0);
        AffineTransform v2 = AffineTransform.getQuadrantRotateInstance(arg1);
        Area v3 = v1.createTransformedArea(v2);
        Rectangle2D v4 = v3.getBounds2D();
        return v4;
    }
}
