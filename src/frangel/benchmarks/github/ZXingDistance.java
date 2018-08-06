package frangel.benchmarks.github;

import com.google.zxing.ResultPoint;
import com.google.zxing.common.detector.MathUtils;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.BenchmarkUtils;
import frangel.benchmarks.TaskCreator;

public enum ZXingDistance implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.GITHUB.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("zxing_distance")
                .setInputTypes(ResultPoint.class, ResultPoint.class)
                .setInputNames("a", "b")
                .setOutputType(float.class)
                .addPackages("com.google.zxing.common")
                .excludeMethods(BenchmarkUtils.getMethod(ResultPoint.class, "distance", ResultPoint.class, ResultPoint.class)) // to synthesize
                .addTags(Tag.SINGLE_LINE);

        // no unit tests

        // added examples
        task.addExample(new Example()
                .setInputs(() -> new Object[] { new ResultPoint(12, 34), new ResultPoint(42, -6) })
                .setOutput(50f));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new ResultPoint(30, 40), new ResultPoint(0, 0) })
                .setOutput(50f));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new ResultPoint(0, 0), new ResultPoint(30, 40) })
                .setOutput(50f));

        return task;
    }

    // from zxing-master/core/src/main/java/com/google/zxing/aztec/detector/Detector.java
    static float solution(ResultPoint a, ResultPoint b) {
        return MathUtils.distance(a.getX(), a.getY(), b.getX(), b.getY());
    }
}
