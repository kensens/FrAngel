package frangel.benchmarks.github;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.TaskCreator;

public enum ElasticSearchMergePaths implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.GITHUB.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("elasticsearch_mergePaths")
                .setInputTypes(String.class, String.class)
                .setInputNames("path", "field")
                .setOutputType(String.class)
                .addLiterals(String.class, ".")
                .addTags(Tag.IF);

        // no unit tests in elasticsearch-master/server/src/test/java/org/elasticsearch/cluster/metadata/MetaDataTests.java

        // added examples
        task.addExample(new Example()
                .setInputs(() -> new Object[] { "path", "field" })
                .setOutput("path.field"));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { "a", "" })
                .setOutput("a."));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { "", "field" })
                .setOutput("field"));

        return task;
    }

    // from elasticsearch-master/server/src/main/java/org/elasticsearch/cluster/metadata/MetaData.java:
    static String solution(String path, String field) {
        if (path.length() == 0) {
            return field;
        }
        return path + "." + field;
    }
}
