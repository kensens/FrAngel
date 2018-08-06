package frangel.benchmarks.sypet;

import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.linear.RealVector;

import com.opengamma.analytics.math.matrix.DoubleMatrix1D;
import com.opengamma.analytics.math.matrix.DoubleMatrix2D;
import com.opengamma.analytics.math.util.wrapper.CommonsMathWrapper;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.TaskCreator;

public enum SyPet_07_getOuterProduct implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.SYPET.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("sypet_07_getOuterProduct")
                .setInputTypes(DoubleMatrix1D.class, DoubleMatrix1D.class)
                .setInputNames("vec1", "vec2")
                .setOutputType(DoubleMatrix2D.class)
                .addPackages("com.opengamma.analytics.math", "org.apache.commons.math.linear")
                .addTags(Tag.SINGLE_LINE); // Easily written in one line

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new DoubleMatrix1D(1, 1, 1), new DoubleMatrix1D(1, 2, 3) })
                .setOutput(new DoubleMatrix2D(new double[][] { { 1, 2, 3 }, { 1, 2, 3 }, { 1, 2, 3 } })));

        return task;
    }

    public static DoubleMatrix2D solution(DoubleMatrix1D arg0, DoubleMatrix1D arg1) {
        RealVector v1 = CommonsMathWrapper.wrap(arg1);
        RealVector v2 = CommonsMathWrapper.wrap(arg0);
        RealMatrix v3 = v2.outerProduct(v1);
        DoubleMatrix2D v4 = CommonsMathWrapper.unwrap(v3);
        return v4;
    }
}
