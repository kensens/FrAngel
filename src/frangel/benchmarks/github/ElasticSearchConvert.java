package frangel.benchmarks.github;

import org.elasticsearch.common.unit.DistanceUnit;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.BenchmarkUtils;
import frangel.benchmarks.TaskCreator;

public enum ElasticSearchConvert implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.GITHUB.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("elasticsearch_convert")
                .setInputTypes(double.class, DistanceUnit.class, DistanceUnit.class)
                .setInputNames("distance", "from", "to")
                .setOutputType(double.class)
                .setDeclaringClass(DistanceUnit.class)
                .excludeMethods(
                        BenchmarkUtils.getMethod(DistanceUnit.class, "convert", double.class, DistanceUnit.class, DistanceUnit.class), // to synthesize
                        BenchmarkUtils.getMethod(DistanceUnit.class, "convert", double.class, DistanceUnit.class), // similar
                        BenchmarkUtils.getMethod(DistanceUnit.Distance.class, "convert", DistanceUnit.class)) // similar
                .addTags(Tag.IF);

        // unit tests from elasticsearch-master/server/src/test/java/org/elasticsearch/common/unit/DistanceUnitTests.java
        task.addExample(new Example()
                .setInputs(() -> new Object[] { 10.0, DistanceUnit.MILES, DistanceUnit.KILOMETERS })
                .setOutput(16.09344));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { 10.0, DistanceUnit.KILOMETERS, DistanceUnit.MILES })
                .setOutput(6.21371192));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { 10.0, DistanceUnit.METERS, DistanceUnit.KILOMETERS })
                .setOutput(0.01));
        // and many more

        // added examples
        task.addExample(new Example()
                .setInputs(() -> new Object[] { 12.34, null, null })
                .setOutput(12.34));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { Double.MAX_VALUE, DistanceUnit.MILES, DistanceUnit.MILES })
                .setOutput(Double.MAX_VALUE)); // not Infinity

        return task;
    }

    static double solution(double distance, DistanceUnit from, DistanceUnit to) {
        return DistanceUnit.convert(distance, from, to);
    }

    // from elasticsearch-master/server/src/main/java/org/elasticsearch/common/unit/DistanceUnit.java
    // DistanceUnit.meters is private so we can't access it here
    /*
    static double convert(double distance, DistanceUnit from, DistanceUnit to) {
        if (from == to) {
            return distance;
        } else {
            return distance * from.meters / to.meters;
        }
    }
     */
}
