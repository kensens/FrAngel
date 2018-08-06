package frangel.benchmarks.github;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.Date;

import com.alibaba.fastjson.serializer.*;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.BenchmarkUtils;
import frangel.benchmarks.TaskCreator;

public enum FastjsonWrite implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.GITHUB.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SerializeConfig.getGlobalInstance().put(FastjsonWrite.class, new CustomSerializer());

        SynthesisTask task = new SynthesisTask()
                .setName("fastjson_write")
                .setInputTypes(SerializeWriter.class, Object.class)
                .setInputNames("out", "object")
                .setOutputType(void.class)
                .addPackages("com.alibaba.fastjson.serializer")
                .excludeMethods(
                        BenchmarkUtils.getMethod(JSONSerializer.class, "write", SerializeWriter.class, Object.class), // to synthesize
                        BenchmarkUtils.getMethod(JSONSerializer.class, "write", Writer.class, Object.class)) // similar
                .addTags(Tag.SINGLE_LINE);// Easily written in one line

        // unit tests from fastjson-master/src/test/java/com/alibaba/json/bvt/serializer/ObjectArraySerializerTest.java
        task.addExample(new Example()
                .setInputs(() -> new Object[] { new SerializeWriter(1), new Object[] { "a12", "b34" } })
                .setModifiedInputChecker(1, (SerializeWriter out) -> {
                    try {
                        return out.toString().equals("[\"a12\",\"b34\"]");
                    } catch (Exception e) {}
                    return false;
                }));
        // (and others, and more unit tests in other files)

        // added examples

        // rejects new JSONSerializer(out).writeWithFormat(object, "")
        task.addExample(new Example()
                .setInputs(() -> new Object[] { new SerializeWriter(1), new Date(123456789L) })
                .setModifiedInputChecker(1, (SerializeWriter out) -> {
                    try {
                        return out.toString().equals("123456789");
                    } catch (Exception e) {}
                    return false;
                }));

        // rejects new JSONSerializer(out).writeWithFieldName(object, "")
        task.addExample(new Example()
                .setInputs(() -> new Object[] { new SerializeWriter(1), this })
                .setModifiedInputChecker(1, (SerializeWriter out) -> {
                    try {
                        return out.toString().equals("\"FastjsonWrite\"");
                    } catch (Exception e) {}
                    return false;
                }));

        return task;
    }

    // from fastjson-master/src/main/java/com/alibaba/fastjson/serializer/JSONSerializer.java
    static void solution(SerializeWriter out, Object object) {
        JSONSerializer serializer = new JSONSerializer(out);
        serializer.write(object);
    }

    static class CustomSerializer implements ObjectSerializer {
        @Override
        public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
            if (fieldName != null || fieldType != null || features != 0)
                throw new IOException("CustomSerializer.write()");
            serializer.write("FastjsonWrite");
        }
    }
}
