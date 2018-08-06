package frangel.benchmarks.github;

import com.alibaba.fastjson.*;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.BenchmarkUtils;
import frangel.benchmarks.TaskCreator;

public enum FastjsonRead implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.GITHUB.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("fastjson_read")
                .setInputTypes(String.class, String.class)
                .setInputNames("json", "path")
                .setOutputType(Object.class)
                .addPackages("com.alibaba.fastjson")
                .excludeMethods(BenchmarkUtils.getMethod(JSONPath.class, "read", String.class, String.class)) // to synthesize
                .addTags(Tag.SINGLE_LINE); // Easily written in one line

        // unit tests from fastjson-master/src/test/java/com/alibaba/json/bvt/path/JSONPath_2.java
        task.addExample(new Example()
                .setInputs(() -> new Object[] {
                        "{\"user\":[{\"amount\":1.11,\"isadmin\":true,\"age\":18},{\"amount\":0.22,\"isadmin\":false,\"age\":28}]}",
                        "$.user[age = 18]"
                })
                .setOutputChecker((Object o) -> {
                    if (!(o instanceof JSONArray))
                        return false;
                    JSONArray array = (JSONArray) o;
                    try {
                        return array.size() == 1 && 1.11D == array.getJSONObject(0).getDoubleValue("amount") &&
                                array.getJSONObject(0).getBoolean("isadmin") && 18 == array.getJSONObject(0).getIntValue("age");
                    } catch (Exception e) {}
                    return false;
                }));
        // (and 4 others)

        // added examples
        task.addExample(new Example()
                .setInputs(() -> new Object[] { "{\"a\":123}", "$" })
                .setOutputChecker((Object o) -> {
                    try {
                        return o instanceof JSONObject &&
                                ((JSONObject) o).containsKey("a") &&
                                ((JSONObject) o).getInteger("a") == 123;
                    } catch (Exception e) {}
                    return false;
                }));

        return task;
    }

    // from fastjson-master/src/main/java/com/alibaba/fastjson/JSONPath.java
    static Object solution(String json, String path) {
        Object object = JSON.parse(json);
        JSONPath jsonpath = JSONPath.compile(path);
        return jsonpath.eval(object);
    }
}
