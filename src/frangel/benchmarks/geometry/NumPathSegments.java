package frangel.benchmarks.geometry;

import java.awt.geom.*;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.TaskCreator;

public enum NumPathSegments implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.GEOMETRY.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("numPathSegments")
                .setInputTypes(PathIterator.class)
                .setInputNames("it")
                .setOutputType(int.class)
                .addTags(Tag.FOR);

        task.addExample(new Example()
                .setInputs(() -> {
                    Area area = new Area(new Rectangle2D.Double(6, 7, 8, 9));
                    area.add(new Area(new Ellipse2D.Double(5, 7, 2, 2)));
                    return new Object[] { area.getPathIterator(null) };
                })
                .setOutput(7));

        task.addExample(new Example()
                .setInputs(() -> {
                    Area area = new Area(new Rectangle2D.Double(6, 7, 8, 9));
                    area.add(new Area(new Ellipse2D.Double(5, 7.1, 2, 2)));
                    return new Object[] { area.getPathIterator(null) };
                })
                .setOutput(8));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Line2D.Double(6, 7, 8, 9).getPathIterator(null) })
                .setOutput(2));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Rectangle2D.Double(6, 7, 8, 9).getPathIterator(null) })
                .setOutput(6));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new RoundRectangle2D.Double(6, 7, 8, 9, 0.5, 0.6).getPathIterator(null) })
                .setOutput(10));

        return task;
    }

    static int solution(PathIterator it) {
        int count = 0;
        for ( ; !it.isDone(); count++) {
            it.next();
        }
        return count;
    }
}
