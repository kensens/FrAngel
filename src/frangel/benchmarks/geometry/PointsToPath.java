package frangel.benchmarks.geometry;

import java.awt.Polygon;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.BenchmarkUtils;
import frangel.benchmarks.TaskCreator;

public enum PointsToPath implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.GEOMETRY.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("pointsToPath")
                .setInputTypes(Point2D[].class)
                .setInputNames("points")
                .setOutputType(Path2D.Double.class)
                .makeInputsImmutable()
                .addEqualityTester(Path2D.Double.class, BenchmarkUtils::equalsPath2D)
                .addTags(Tag.FOREACH);

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Point2D[] {} })
                .setOutput(new Path2D.Double(new Polygon(new int[] {0}, new int[] {0}, 1))));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Point2D[] {
                        new Point2D.Double(1, 0)
                }})
                .setOutput(new Path2D.Double(new Polygon(new int[] {0, 1}, new int[] {0, 0}, 2))));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Point2D[] {
                        new Point2D.Double(0, 1)
                }})
                .setOutput(new Path2D.Double(new Polygon(new int[] {0, 0}, new int[] {0, 1}, 2))));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Point2D[] {
                        new Point2D.Double(1, 1)
                }})
                .setOutput(new Path2D.Double(new Polygon(new int[] {0, 1}, new int[] {0, 1}, 2))));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Point2D[] {
                        new Point2D.Double(123, 0)
                }})
                .setOutput(new Path2D.Double(new Polygon(new int[] {0, 123}, new int[] {0, 0}, 2))));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Point2D[] {
                        new Point2D.Double(0, 456)
                }})
                .setOutput(new Path2D.Double(new Polygon(new int[] {0, 0}, new int[] {0, 456}, 2))));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Point2D[] {
                        new Point2D.Double(123, 456)
                }})
                .setOutput(new Path2D.Double(new Polygon(new int[] {0, 123}, new int[] {0, 456}, 2))));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Point2D[] {
                        new Point2D.Double(1.2, 0.0),
                        new Point2D.Double(1.2, 3.4),
                        new Point2D.Double(0.0, 3.4),
                        new Point2D.Double()
                }})
                .setOutput(new Path2D.Double(new Rectangle2D.Double(0, 0, 1.2, 3.4))));

        return task;
    }

    static Path2D.Double solution(Point2D[] points) {
        Path2D.Double path = new Path2D.Double();
        path.moveTo(0, 0);
        for (Point2D p : points)
            path.lineTo(p.getX(), p.getY());
        path.closePath();
        return path;
    }
}
