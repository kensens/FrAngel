package frangel.benchmarks.github;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.TaskCreator;

public enum ElasticSearchSplit implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.GITHUB.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("elasticsearch_split")
                .setInputTypes(String.class, int.class, String[].class)
                .setInputNames("toSplit", "index", "result")
                .setOutputType(String[].class)
                .addTags(/* none applicable */);

        // no unit tests

        // added examples
        task.addExample(new Example()
                .setInputs(() -> new Object[] { "Hello world!", 5, new String[] {"some", "existing", "junk"} })
                .setOutput(new String[] {"Hello", "world!", "junk"}));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { "Hello world!", 11, new String[] {"some", "", "junk"}})
                .setOutput(new String[] {"Hello world", "", "junk"}));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { "Hello world!", 0, new String[] {"", "existing", "junk"}})
                .setOutput(new String[] {"", "ello world!", "junk"}));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { " ", 0, new String[] {"some", "", "junk"}})
                .setOutput(new String[] {"", "", "junk"}));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { " ", 0, new String[] {"", "existing", "junk"}})
                .setOutput(new String[] {"", "", "junk"}));

        return task;
    }

    // from elasticsearch-master/server/src/main/java/org/elasticsearch/search/aggregations/support/AggregationPath.java
    static String[] solution(String toSplit, int index, String[] result) {
        result[0] = toSplit.substring(0, index);
        result[1] = toSplit.substring(index + 1);
        return result;
    }
}
