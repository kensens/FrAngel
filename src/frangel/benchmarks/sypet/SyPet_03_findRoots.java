package frangel.benchmarks.sypet;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.analysis.solvers.LaguerreSolver;
import org.apache.commons.math3.complex.Complex;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.TaskCreator;

public enum SyPet_03_findRoots implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.SYPET.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("sypet_03_findRoots")
                .setInputTypes(PolynomialFunction.class, double.class)
                .setInputNames("func", "init")
                .setOutputType(Complex[].class)
                .addPackages("org.apache.commons.math3")
                .addTags(Tag.SINGLE_LINE); // Easily written in one line

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new PolynomialFunction(new double[] { 1, -2, 1 }), 0.0 })
                .setOutputChecker((Complex[] comp) -> comp != null && comp.length == 2 && comp[0].getReal() == 1.0 && comp[0].getImaginary() == 0.0));

        return task;
    }

    public static Complex[] solution(PolynomialFunction arg0, double arg1) {
        LaguerreSolver v1 = new LaguerreSolver();
        double[] v2 = arg0.getCoefficients();
        Complex[] v3 = v1.solveAllComplex(v2, arg1);
        return v3;
    }
}
