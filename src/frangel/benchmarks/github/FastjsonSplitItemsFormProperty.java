package frangel.benchmarks.github;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.TaskCreator;

public enum FastjsonSplitItemsFormProperty implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.GITHUB.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("fastjson_splitItemsFormProperty")
                .setInputTypes(String.class)
                .setInputNames("property")
                .setOutputType(String[].class)
                .addLiterals(String.class, ",")
                .addTags(Tag.IF);

        // no unit tests

        // added examples
        task.addExample(new Example()
                .setInputs(() -> new Object[] { "abc,de,12345" })
                .setOutput(new String[] {"abc", "de", "12345"}));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { "," })
                .setOutput(new String[] {}));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { " " })
                .setOutput(new String[] {" "}));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { "" })
                .setOutput(null));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { null })
                .setOutput(null));

        return task;
    }

    // from fastjson-master/src/main/java/com/alibaba/fastjson/parser/ParserConfig.java
    static String[] solution(final String property){
        if (property != null && property.length() > 0) {
            return property.split(",");
        }
        return null;
    }
}
