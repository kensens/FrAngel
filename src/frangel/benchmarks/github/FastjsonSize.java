package frangel.benchmarks.github;

import java.util.Collections;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.BenchmarkUtils;
import frangel.benchmarks.TaskCreator;

public enum FastjsonSize implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.GITHUB.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("fastjson_size")
                .setInputTypes(Object.class, String.class)
                .setInputNames("rootObject", "path")
                .setOutputType(int.class)
                .addPackages("com.alibaba.fastjson")
                .excludeMethods(BenchmarkUtils.getMethod(JSONPath.class, "size", Object.class, String.class)) // to synthesize
                .addTags(/* none applicable */);

        // unit tests from fastjson-master/src/test/java/com/alibaba/json/bvt/path/JSONPath_size.java (simplified)
        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeList("abc", "def", 123), "$" })
                .setOutput(3));

        task.addExample(new Example()
                .setInputs(() -> new Object[] {
                        new JSONObject().fluentPut("values", BenchmarkUtils.makeList("abc", "def", 123)),
                        "$.values"
                })
                .setOutput(3));

        // added examples
        task.addExample(new Example()
                .setInputs(() -> new Object[] { Collections.nCopies(111, "foo"), "$" })
                .setOutput(111));

        return task;
    }

    static int solution(Object rootObject, String path) {
        return JSONPath.size(rootObject, path);
    }

    /*
    // from fastjson-master/src/main/java/com/alibaba/fastjson/JSONPath.java
    // JSONPath.evalSize(Object) isn't visible
    static int size(Object rootObject, String path) {
        JSONPath jsonpath = compile(path);
        Object result = jsonpath.eval(rootObject);
        return jsonpath.evalSize(result);
    }
     */
}
