package frangel.experiments;

import java.io.File;
import java.io.IOException;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import frangel.SynthesisTask;
import frangel.utils.Utils;

public class SyPetBenchmark {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String SYPET_FRANGEL_BENCHMARKS_DIR = "../sypet/frangel-benchmarks/";
    private static final String SYPET_CUSTOM_LIB_DIR = "../sypet/custom-lib/";

    int id;
    String methodName;
    List<String> paramNames;
    List<String> srcTypes;
    String tgtType;
    List<String> packages;
    List<String> libs;
    String group;

    SyPetBenchmark(SynthesisTask task) {
        id = 0; // not relevant
        methodName = task.getName();
        boolean isSypet = methodName.toLowerCase().startsWith("sypet");
        paramNames = new ArrayList<>();
        srcTypes = new ArrayList<>();
        for (Class<?> cls : task.getInputTypes()) {
            paramNames.add("arg" + (paramNames.size() + 1));
            srcTypes.add(getNameForSypet(cls));
        }
        tgtType = getNameForSypet(task.getOutputType());
        if (tgtType.equals("void"))
            tgtType = getNameForSypet(task.getInputTypes()[0]); // SyPet can't handle void return types, so return first argument type

        packages = new ArrayList<>();
        for (Class<?> cls : task.getClasses())
            if (!cls.isPrimitive() && !cls.isArray())
                packages.add(cls.getName());
        if (!isSypet) {
            packages.add("custom.Operators"); // Contains functions for operators, only for non-SyPet benchmarks
            packages.add("custom.Constants_" + methodName); // Contains constants, only for non-SyPet benchmarks
        }
        Collections.sort(packages);

        libs = new ArrayList<>();
        group = task.getGroup();

        if (isSypet) {
            System.out.println("SyPet benchmark: " + methodName);
            String num = methodName.split("_")[1];
            if (num.startsWith("0"))
                num = num.substring(1);
            String[] sypetGroups = {"math", "geometry", "joda", "xml"};
            String filename = null;
            for (String group : sypetGroups) {
                File f = new File("../sypet/benchmarks/" + group + "/" + num + "/benchmark" + num + ".json");
                if (f.exists()) {
                    try {
                        filename = f.getCanonicalPath().toString();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
            if (filename == null) {
                System.err.println("Cannot find SyPet benchmark for " + methodName);
                System.exit(1);
            }
            String json = Utils.stringFromFile(filename);
            int start = json.indexOf('[', json.indexOf("\"packages\":")) + 1;
            int end = json.indexOf(']', start);
            String[] split = json.substring(start, end).replace(",", "").replace("\"", "").trim().split("\\s+");
            List<String> packs = new ArrayList<>(Arrays.asList(split));
            System.out.println("    found packages: " + packs);

            // add SyPet's buildinPkg values
            packs.add("java.io.StringReader");
            packs.add("org.apache.commons.math.stat.regression");
            packs.add("org.apache.commons.math3.geometry.euclidean.twod");

            for (String cls : new ArrayList<>(packages)) {
                boolean good = false;
                for (String p : packs) {
                    if (cls.startsWith(p)) {
                        good = true;
                        break;
                    }
                }
                if (!good) {
                    System.out.println("    removing class: " + cls);
                    packages.remove(cls);
                }
            }
        }

        String filename = SYPET_FRANGEL_BENCHMARKS_DIR + group + "/" + methodName + ".json";
        writeToFile(filename);
        if (!isSypet)
            writeConstants(task);
    }

    private String getNameForSypet(Class<?> cls) {
        if (cls.isMemberClass())
            return cls.getName();
        return cls.getCanonicalName();
    }

    private void writeToFile(String filename) {
        Utils.stringToFile(GSON.toJson(this), filename);
        System.out.println("wrote " + filename);
    }

    private void writeConstants(SynthesisTask task) {
        Map<Class<?>, List<Object>> constants = task.getLiterals();
        String str = "// Generated by SyPetBenchmark.writeConstants()\npackage custom;\npublic class Constants_" + methodName + " {\n";
        int count = 0;
        List<Class<?>> keys = new ArrayList<>(constants.keySet());
        keys.sort((Class<?> c1, Class<?> c2) -> c1.getName().compareTo(c2.getName()));
        for (Class<?> cls : keys)
            for (Object o : constants.get(cls))
                str += "\tpublic static " + cls.getName() + " constant" + (count++) + "() { return " + toCode(cls, o) + "; }\n";
        str += "}\n";
        String filename = SYPET_CUSTOM_LIB_DIR + "Constants_" + methodName + ".java";
        Utils.stringToFile(str, filename);
        System.out.println("wrote " + filename);
    }

    private String toCode(Class<?> cls, Object o) {
        if (o == null)
            return "null";
        if (o instanceof String)
            return Utils.toStringLiteral((String) o);
        if (cls.equals(char.class))
            return Utils.toCharLiteral((char) o);
        if (cls.equals(float.class) || cls.equals(Float.class))
            return o.toString() + "F";
        if (cls.equals(long.class) || cls.equals(Long.class))
            return o.toString() + "L";
        return o.toString();
    }
}
