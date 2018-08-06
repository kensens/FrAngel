package frangel.benchmarks.geometry;

import java.awt.Polygon;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.BenchmarkUtils;
import frangel.benchmarks.TaskCreator;

public enum ScalePolygon implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.GEOMETRY.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("scalePolygon")
                .setInputTypes(Polygon.class, int.class, int.class)
                .setInputNames("poly", "scaleX", "scaleY")
                .setOutputType(void.class)
                .addEqualityTester(Polygon.class, BenchmarkUtils::equalsPolygon)
                .addTags(Tag.FOR);

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Polygon(new int[] {11, 12, 13, -5}, new int[] {15, 0, -10, 11}, 4), -5, 6 })
                .setModifiedInput(1, new Polygon(new int[] {-55, -60, -65, 25}, new int[] {90, 0, -60, 66}, 4)));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Polygon(new int[] {11, 12}, new int[] {-10, 11}, 2), -5, 6 })
                .setModifiedInput(1, new Polygon(new int[] {-55, -60}, new int[] {-60, 66}, 2)));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Polygon(new int[] {11}, new int[] {15}, 1), -5, 6 })
                .setModifiedInput(1, new Polygon(new int[] {-55}, new int[] {90}, 1)));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Polygon(new int[] {0}, new int[] {15}, 1), -5, 6 })
                .setModifiedInput(1, new Polygon(new int[] {0}, new int[] {90}, 1)));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Polygon(new int[] {11}, new int[] {0}, 1), -5, 6 })
                .setModifiedInput(1, new Polygon(new int[] {-55}, new int[] {0}, 1)));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Polygon(new int[] {0}, new int[] {1}, 1), 0, 123 })
                .setModifiedInput(1, new Polygon(new int[] {0}, new int[] {123}, 1)));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Polygon(new int[] {1}, new int[] {0}, 1), 123, 0 })
                .setModifiedInput(1, new Polygon(new int[] {123}, new int[] {0}, 1)));

        return task;
    }

    static void solution(Polygon poly, int scaleX, int scaleY) {
        for (int i = 0; i < poly.npoints; i++) {
            poly.xpoints[i] *= scaleX;
            poly.ypoints[i] *= scaleY;
        }
    }
}
