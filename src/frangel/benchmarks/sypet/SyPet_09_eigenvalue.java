package frangel.benchmarks.sypet;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.RealMatrix;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.BenchmarkUtils;
import frangel.benchmarks.TaskCreator;

public enum SyPet_09_eigenvalue implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.SYPET.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("sypet_09_eigenvalue")
                .setInputTypes(RealMatrix.class, int.class)
                .setInputNames("mat", "index")
                .setOutputType(Vector2D.class)
                .addPackages("org.apache.commons.math3.linear", "org.apache.commons.math3.geometry.euclidean.twod")
                .addEqualityTester(Vector2D.class, BenchmarkUtils::equalsVector2D)
                .addTags(/* none applicable */); // Would have to create EigenDecomposition twice for single-line solution

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Array2DRowRealMatrix(new double[][] { { 0, -20 }, { 10, 10 } }), 0 })
                .setOutput(new Vector2D(5, 5*Math.sqrt(7))));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Array2DRowRealMatrix(new double[][] { { 0, 2 }, { 2, 0 } }), 1 })
                .setOutput(new Vector2D(-2, 0)));

        return task;
    }

    public static Vector2D solution(RealMatrix arg0, int arg1) {
        EigenDecomposition v1 = new EigenDecomposition(arg0);
        double v2 = v1.getImagEigenvalue(arg1);
        double v3 = v1.getRealEigenvalue(arg1);
        Vector2D v4 = new Vector2D(v3, v2);
        return v4;
    }
}
