package frangel.benchmarks.geometry;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.BenchmarkUtils;
import frangel.benchmarks.TaskCreator;

public enum MapPointCoordinates implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.GEOMETRY.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("mapPointCoordinates")
                .setInputTypes(Point2D.class, Map.class)
                .addGenerics(Map.class, Double.class)
                .setInputNames("point", "map")
                .setOutputType(void.class)
                .addEqualityTester(Point2D.class, BenchmarkUtils::equalsPoint2D)
                .makeInputsImmutable()
                .addTags(Tag.SINGLE_LINE);

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Point2D.Double(-100.5, 34.0), getMap() })
                .setModifiedInput(1, new Point2D.Double(10.5, 56.0)));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Point2D.Double(34.0, 10.01), getMap() })
                .setModifiedInput(1, new Point2D.Double(56.0, 0.0)));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Point2D.Double(10.01, 34.0), getMap() })
                .setModifiedInput(1, new Point2D.Double(0.0, 56.0)));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Point2D.Double(11.1, 10.01), getMap() })
                .setModifiedInput(1, new Point2D.Double(11.1, 0.0)));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Point2D.Double(10.01, 11.1), getMap() })
                .setModifiedInput(1, new Point2D.Double(0.0, 11.1)));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Point2D.Double(10.01, 0.0), getMap() })
                .setModifiedInput(1, new Point2D.Double(0.0, 77.7)));

        return task;
    }

    static Map<Double, Double> getMap() {
        Map<Double, Double> map = new HashMap<>();
        map.put(34.0, 56.0);
        map.put(-100.5, 10.5);
        map.put(11.1, 11.1);
        map.put(10.01, 0.0);
        map.put(0.0, 77.7);
        return map;
    }

    static void solution(Point2D point, Map<Double, Double> map) {
        point.setLocation(map.get(point.getX()), map.get(point.getY()));
    }
}
