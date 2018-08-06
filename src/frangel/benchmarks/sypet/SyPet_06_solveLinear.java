package frangel.benchmarks.sypet;

import org.apache.commons.math3.linear.*;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.TaskCreator;

public enum SyPet_06_solveLinear implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.SYPET.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("sypet_06_solveLinear")
                .setInputTypes(double[][].class, double[].class)
                .setInputNames("mat", "vec")
                .setOutputType(double[].class)
                .addPackages("org.apache.commons.math3.linear")
                .addTags(Tag.SINGLE_LINE); // Easily written in one line

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new double[][]{{3,-1},{1,1}}, new double[]{3,5} })
                .setOutput(new double[]{2, 3}));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new double[][]{{1,2,1},{2,-1,3},{3,1,2}}, new double[]{7,7,18} })
                .setOutput(new double[]{7, 1, -2}));

        return task;
    }

    public static double[] solution(double[][] arg0, double[] arg1) {
        RealMatrix v1 = MatrixUtils.createRealMatrix(arg0);
        RealMatrix v2 = v1.transpose();
        LUDecomposition v3 = new LUDecomposition(v2);
        DecompositionSolver v4 = v3.getSolver();
        RealMatrix v5 = v4.getInverse();
        double[] v6 = v5.preMultiply(arg1);
        return v6;
    }
}
