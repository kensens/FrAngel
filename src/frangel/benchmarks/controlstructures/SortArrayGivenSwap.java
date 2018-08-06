package frangel.benchmarks.controlstructures;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.TaskCreator;

public enum SortArrayGivenSwap implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.CONTROL_STRUCTURES.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("sortArrayGivenSwap")
                .setInputTypes(double[].class)
                .setInputNames("arr")
                .setOutputType(void.class)
                .addClasses(Swap.class)
                .addTags(Tag.FOR, Tag.IF);

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new double[] {7, 3, 6, 5, 2, 1, 4} })
                .setModifiedInput(1, new double[] {1, 2, 3, 4, 5, 6, 7}));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new double[] {3, 1, 2, 5, 4} })
                .setModifiedInput(1, new double[] {1, 2, 3, 4, 5}));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new double[] {5, 1, 2, 3, 4} })
                .setModifiedInput(1, new double[] {1, 2, 3, 4, 5}));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new double[] {3.4, 1.2} })
                .setModifiedInput(1, new double[] {1.2, 3.4}));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new double[] {1.2, 1.2} })
                .setModifiedInput(1, new double[] {1.2, 1.2}));

        return task;
    }

    static void solution(double[] arr) {
        for (int i = 0; i < arr.length; i++)
            for (int j = 0; j < arr.length - 1; j++)
                if (arr[j+1] < arr[j])
                    Swap.swap(arr, j, j + 1);
    }

    static class Swap {
        public static void swap(double[] arr, int i, int j) {
            double d = arr[i];
            arr[i] = arr[j];
            arr[j] = d;
        }
    }
}
