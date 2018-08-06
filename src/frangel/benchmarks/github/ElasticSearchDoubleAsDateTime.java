package frangel.benchmarks.github;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.TaskCreator;

public enum ElasticSearchDoubleAsDateTime implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.GITHUB.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("elasticsearch_doubleAsDateTime")
                .setInputTypes(Double.class)
                .setInputNames("d")
                .setOutputType(DateTime.class)
                .addClasses(org.joda.time.DateTimeZone.class)
                .addTags(Tag.IF);

        // no unit tests

        // added examples
        task.addExample(new Example()
                .setInputs(() -> new Object[] { 123456789.0 })
                .setOutput(new DateTime(123456789, DateTimeZone.UTC)));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { Double.POSITIVE_INFINITY })
                .setOutput(null));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { Double.NEGATIVE_INFINITY })
                .setOutput(null));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { null })
                .setOutput(null));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { Double.NaN })
                .setOutput(new DateTime(0, DateTimeZone.UTC)));

        return task;
    }

    // from elasticsearch-master/server/src/main/java/org/elasticsearch/search/aggregations/bucket/range/ParsedDateRange.java
    static DateTime solution(Double d) {
        if (d == null || Double.isInfinite(d)) {
            return null;
        }
        return new DateTime(d.longValue(), DateTimeZone.UTC);
    }
}
