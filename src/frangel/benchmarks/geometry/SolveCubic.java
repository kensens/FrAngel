package frangel.benchmarks.geometry;

import java.awt.geom.CubicCurve2D;
import java.util.*;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.BenchmarkUtils;
import frangel.benchmarks.TaskCreator;

public enum SolveCubic implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.GEOMETRY.register(INSTANCE);
    }

    public static boolean equalsUnorderedList(List<Double> list, double... values) {
        if (list.size() != values.length)
            return false;
        if (list.contains(null))
            return false;
        Collections.sort(list);
        Arrays.sort(values);
        for (int i = 0; i < list.size(); i++)
            if (!BenchmarkUtils.equalsDouble(list.get(i), values[i]))
                return false;
        return true;
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("solveCubic")
                .setInputTypes(double[].class, List.class)
                .addGenerics(List.class, Double.class)
                .setInputNames("coeff", "roots")
                .setOutputType(int.class)
                .addPackages("java.awt.geom")
                .addTags(Tag.FOR);


        task.addExample(new Example()
                .setInputs(() -> new Object[] { new double[] { 47.25, -8.25, -5, 1 }, new ArrayList<Double>() })
                .setModifiedInputChecker(2, (List<Double> list) -> SolveCubic.equalsUnorderedList(list, -3.0, 3.5, 4.5))
                .setOutput(3));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new double[] { 0.714, -1.79, 0.4, 1 }, new ArrayList<Double>() })
                .setModifiedInputChecker(2, (List<Double> list) -> SolveCubic.equalsUnorderedList(list, -1.7, 0.6, 0.7))
                .setOutput(3));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new double[] { 0.748, -1.77, 0.4, 1 }, new ArrayList<Double>() })
                .setModifiedInput(2, BenchmarkUtils.makeList(-1.7))
                .setOutput(1));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new double[] { 0.21, -0.65, 0.5, 0 }, new ArrayList<Double>() })
                .setModifiedInputChecker(2, (List<Double> list) -> SolveCubic.equalsUnorderedList(list, 0.6, 0.7))
                .setOutput(2));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new double[] { 12.3, -10.0, 0, 0 }, new ArrayList<Double>() })
                .setModifiedInput(2, BenchmarkUtils.makeList(1.23))
                .setOutput(1));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new double[] { 0.22, -0.65, 0.5, 0 }, new ArrayList<Double>() })
                .setModifiedInput(2, BenchmarkUtils.makeList())
                .setOutput(0));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new double[] { 0, -0.65, 0, 0 }, new ArrayList<Double>() })
                .setModifiedInput(2, BenchmarkUtils.makeList(0.0))
                .setOutput(1));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new double[] { 0.21, 0, 0, 0 }, new ArrayList<Double>() })
                .setModifiedInput(2, BenchmarkUtils.makeList())
                .setOutput(0));

        return task;
    }

    static int solution(double[] coeff, List<Double> roots) {
        int ans = CubicCurve2D.solveCubic(coeff);
        for (int i = 0; i < ans; i++)
            roots.add(coeff[i]);
        return roots.size();
    }
}
