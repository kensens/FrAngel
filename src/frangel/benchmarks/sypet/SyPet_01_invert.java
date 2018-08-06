package frangel.benchmarks.sypet;

import org.apache.commons.math3.linear.*;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.BenchmarkUtils;
import frangel.benchmarks.TaskCreator;

public enum SyPet_01_invert implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.SYPET.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("sypet_01_invert")
                .setInputTypes(RealMatrix.class)
                .setInputNames("mat")
                .setOutputType(RealMatrix.class)
                .addPackages("org.apache.commons.math3.linear")
                .addEqualityTester(RealMatrix.class, BenchmarkUtils::equalsRealMatrix)
                .addTags(Tag.SINGLE_LINE); // Easily written in one line

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Array2DRowRealMatrix(new double[][]{{1,2,3},{4,5,6}}) })
                .setOutput(new Array2DRowRealMatrix(new double[][]{{-0.944444,0.444444},{-0.111111,0.111111},{0.722222,-0.222222}})));

        return task;
    }

    public static RealMatrix solution(RealMatrix arg0) {
        SingularValueDecomposition v1 = new SingularValueDecomposition(arg0);
        DecompositionSolver v2 = v1.getSolver();
        RealMatrix v3 = v2.getInverse();
        return v3;
    }
}
