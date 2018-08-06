package frangel.benchmarks.sypet;

import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.linear.SingularValueDecomposition;
import org.apache.commons.math.linear.SingularValueDecompositionImpl;

import com.opengamma.analytics.math.linearalgebra.SVDecompositionCommonsResult;
import com.opengamma.analytics.math.matrix.DoubleMatrix2D;
import com.opengamma.analytics.math.util.wrapper.CommonsMathWrapper;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.TaskCreator;

public enum SyPet_04_evaluate implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.SYPET.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("sypet_04_evaluate")
                .setInputTypes(DoubleMatrix2D.class)
                .setInputNames("mat")
                .setOutputType(SVDecompositionCommonsResult.class)
                .addPackages("com.opengamma.analytics.math", "org.apache.commons.math.linear")
                .addTags(Tag.SINGLE_LINE); // Easily written in one line

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new DoubleMatrix2D(new double[][]{{1,2},{2,2},{2,1}}) })
                .setOutputChecker((SVDecompositionCommonsResult res2) -> {
                    if (res2 == null)
                        return false;

                    double inv_sqrt2 = 1/Math.sqrt(2);
                    double[][] UMat = new double[][]{{3/Math.sqrt(34),-1/Math.sqrt(2)},{4/Math.sqrt(34),0},{3/Math.sqrt(34),1/Math.sqrt(2)}};
                    double[][] SMat = new double[][]{{Math.sqrt(17),0},{0,1}};
                    double[][] VTMat = new double[][]{{inv_sqrt2,inv_sqrt2},{inv_sqrt2,-inv_sqrt2}};

                    double[][] UMatRes = res2.getU().toArray();
                    double[][] SMatRes = res2.getS().toArray();
                    double[][] VTMatRes = res2.getVT().toArray();

                    if(UMat.length != UMatRes.length ||
                            SMat.length != SMatRes.length ||
                            VTMat.length != VTMatRes.length){
                        return false;
                    }

                    for(int i=0; i<UMat.length; i++){
                        for(int j=0;j<UMat[i].length; j++){
                            if(Math.abs(UMat[i][j]-UMatRes[i][j])>0.000005)
                                return false;
                        }
                    }

                    for(int i=0; i<SMat.length; i++){
                        for(int j=0;j<SMat[i].length; j++){
                            if(Math.abs(SMat[i][j]-SMatRes[i][j])>0.000005)
                                return false;
                        }
                    }

                    for(int i=0; i<VTMat.length; i++){
                        for(int j=0;j<VTMat[i].length; j++){
                            if(Math.abs(VTMat[i][j]-VTMatRes[i][j])>0.000005)
                                return false;
                        }
                    }

                    return true;
                }));

        return task;
    }

    public static SVDecompositionCommonsResult solution(DoubleMatrix2D arg0) {
        RealMatrix v1 = CommonsMathWrapper.wrap(arg0);
        SingularValueDecomposition v2 = new SingularValueDecompositionImpl(v1);
        SVDecompositionCommonsResult v3 = new SVDecompositionCommonsResult(v2);
        return v3;
    }
}
