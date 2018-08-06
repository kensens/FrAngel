package frangel.experiments;

import frangel.SynthesisTask;
import frangel.benchmarks.BenchmarkGroup;

public class CreateSyPetBenchmarks {
    public static void main(String[] args) {
        for (BenchmarkGroup group : BenchmarkGroup.values()) {
            for (SynthesisTask task : group.getTasks()) {
                task.finalizeSetup();
                new SyPetBenchmark(task);
            }
        }
    }
}
