package frangel.benchmarks.sypet;

import org.apache.commons.math.linear.RealVector;

import com.opengamma.analytics.math.matrix.DoubleMatrix1D;
import com.opengamma.analytics.math.util.wrapper.CommonsMathWrapper;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.TaskCreator;

public enum SyPet_02_getInnerProduct implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.SYPET.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("sypet_02_getInnerProduct")
                .setInputTypes(DoubleMatrix1D.class, DoubleMatrix1D.class)
                .setInputNames("vec1", "vec2")
                .setOutputType(double.class)
                .addPackages("org.apache.commons.math.linear", "com.opengamma.analytics.math")
                .addTags(Tag.SINGLE_LINE); // Easily written in one line

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new DoubleMatrix1D(1, 1, 1), new DoubleMatrix1D(1, 2, 3) })
                .setOutput(6.0));

        return task;
    }

    static double solution(DoubleMatrix1D arg0, DoubleMatrix1D arg1) {
        double[] v3 = arg1.toArray();
        RealVector v4 = CommonsMathWrapper.wrap(arg0);
        double var5 = v4.dotProduct(v3);
        return var5;
    }
}
