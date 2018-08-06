package frangel.benchmarks.github;

import com.google.zxing.ResultPoint;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.TaskCreator;

public enum ZXingCopyToResult implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.GITHUB.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("zxing_copyToResult")
                .setInputTypes(ResultPoint[].class, ResultPoint[].class, int[].class)
                .setInputNames("result", "tmpResult", "destinationIndexes")
                .setOutputType(void.class)
                .makeInputsImmutable()
                .addTags(Tag.FOR);

        // no unit tests

        // added examples
        task.addExample(new Example()
                .setInputs(() -> new Object[] {
                        new ResultPoint[4],
                        new ResultPoint[] { new ResultPoint(1, 2), new ResultPoint(3, 4), new ResultPoint(12, 23), new ResultPoint(34, 45) },
                        new int[] {2, 0, 3, 1}
                })
                .setModifiedInput(1, new ResultPoint[] { new ResultPoint(3, 4), new ResultPoint(34, 45), new ResultPoint(1, 2), new ResultPoint(12, 23) }));

        task.addExample(new Example()
                .setInputs(() -> new Object[] {
                        new ResultPoint[5],
                        new ResultPoint[] { new ResultPoint(1, 2), new ResultPoint(3, 4) },
                        new int[] {3}
                })
                .setModifiedInput(1, new ResultPoint[] { null, null, null, new ResultPoint(1, 2), null }));

        task.addExample(new Example()
                .setInputs(() -> new Object[] {
                        new ResultPoint[1],
                        new ResultPoint[] { new ResultPoint(1, 2) },
                        new int[] {0}
                })
                .setModifiedInput(1, new ResultPoint[] { new ResultPoint(1, 2) }));

        return task;
    }

    // from zxing-master/core/src/main/java/com/google/zxing/pdf417/detector/Detector.java
    static void solution(ResultPoint[] result, ResultPoint[] tmpResult, int[] destinationIndexes) {
        for (int i = 0; i < destinationIndexes.length; i++) {
            result[destinationIndexes[i]] = tmpResult[i];
        }
    }
}