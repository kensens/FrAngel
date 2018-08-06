package frangel.benchmarks.sypet;

import org.apache.commons.math.linear.RealMatrix;

import com.opengamma.analytics.math.matrix.DoubleMatrix2D;
import com.opengamma.analytics.math.util.wrapper.CommonsMathWrapper;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.TaskCreator;

public enum SyPet_05_invert implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.SYPET.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("sypet_05_invert")
                .setInputTypes(DoubleMatrix2D.class)
                .setInputNames("mat")
                .setOutputType(DoubleMatrix2D.class)
                .addPackages("com.opengamma.analytics.math", "org.apache.commons.math.linear")
                .addTags(Tag.SINGLE_LINE); // Easily written in one line;

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new DoubleMatrix2D(new double[][]{{1,2},{3,4}}) })
                .setOutputChecker((DoubleMatrix2D res2) -> {
                    if(res2 == null)
                        return false;

                    double[][] mat2 = new double[][]{{-2,1},{1.5,-0.5}};
                    double[][] res = res2.toArray();

                    if(mat2.length != res.length || mat2[0].length != res[0].length)
                        return false;

                    for(int i=0; i<mat2.length; i++){
                        for(int j=0; j<mat2[0].length; j++){
                            if(Math.abs(mat2[i][j] - res[i][j]) > 0.00000005){
                                return false;
                            }
                        }
                    }

                    return true;
                }));

        return task;
    }

    @SuppressWarnings("deprecation")
    public static DoubleMatrix2D solution(DoubleMatrix2D arg0) {
        RealMatrix v1 = CommonsMathWrapper.wrap(arg0);
        RealMatrix v2 = v1.inverse();
        DoubleMatrix2D v3 = CommonsMathWrapper.unwrap(v2);
        return v3;
    }
}
