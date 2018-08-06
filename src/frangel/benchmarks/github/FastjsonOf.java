package frangel.benchmarks.github;

import com.alibaba.fastjson.parser.Feature;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.BenchmarkUtils;
import frangel.benchmarks.TaskCreator;

public enum FastjsonOf implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.GITHUB.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("fastjson_of")
                .setInputTypes(Feature[].class)
                .setInputNames("features")
                .setOutputType(int.class)
                .excludeMethods(BenchmarkUtils.getMethod(Feature.class, "of", Feature[].class)) // to synthesize
                .addTags(Tag.IF, Tag.FOREACH);

        // no unit tests

        // added examples

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Feature[] {
                        Feature.AllowArbitraryCommas,    // 6
                        Feature.AutoCloseSource,         // 0
                        Feature.UseBigDecimal,           // 7
                        Feature.AllowArbitraryCommas,    // 6
                        Feature.AllowUnQuotedFieldNames, // 2
                } })
                .setOutput(0xc5));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Feature[] {Feature.AllowArbitraryCommas} })
                .setOutput(1 << 6));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Feature[] {} })
                .setOutput(0));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { null })
                .setOutput(0));

        return task;
    }

    // from fastjson-master/src/main/java/com/alibaba/fastjson/parser/Feature.java
    static int solution(Feature[] features) {
        if (features == null) {
            return 0;
        }
        int value = 0;
        for (Feature feature: features) {
            value |= feature.mask;
        }
        return value;
    }
}
