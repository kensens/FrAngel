package frangel;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.*;

import frangel.utils.Utils;

public class Excluded {
    static {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Class.class, new JsonClassAdapter())
                .registerTypeAdapter(ClassOrPackage.class, new JsonClassOrPackageAdapter())
                .create();
        instance = null;
        try {
            instance = gson.fromJson(new FileReader(Settings.EXCLUDED_JSON_FILE), Excluded.class);
        } catch (FileNotFoundException e) {
            System.err.println("Warning: cannot find excluded.json");
            instance = new Excluded();
        } catch (Exception e) {
            e.printStackTrace();
            instance = new Excluded();
        }
    }

    private static Excluded instance;

    public static boolean isClassExcluded(Class<?> cls) {
        for (ExcludedClasses ec : instance.classes)
            if (ec.matches(cls))
                return true;
        return false;
    }
    public static boolean isMethodExcluded(Method m, Class<?> cls) {
        for (ExcludedMethods em : instance.methods)
            if (em.matches(m, cls))
                return true;
        return false;
    }
    public static boolean isConstructorExcluded(Constructor<?> c, Class<?> cls) {
        for (ExcludedConstructors ec : instance.constructors)
            if (ec.matches(c, cls))
                return true;
        return false;
    }

    private static class ClassOrPackage {
        Class<?> cls;
        String pkg; // without wildcard symbol
        boolean isClass;
        ClassOrPackage(Class<?> cls) {
            this.cls = cls;
            isClass = true;
        }
        ClassOrPackage(String pkg) {
            this.pkg = pkg;
            isClass = false;
        }
        boolean matches(Class<?> c) {
            return isClass ? cls.isAssignableFrom(c) : c.getName().startsWith(pkg);
        }
    }
    private static class JsonClassAdapter implements JsonDeserializer<Class<?>> {
        @Override
        public Class<?> deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            try {
                return Utils.classFromString(json.getAsString());
            } catch (ClassNotFoundException e) {
                e.printStackTrace(System.err);
                return null;
            }
        }
    }
    private static class JsonClassOrPackageAdapter implements JsonDeserializer<ClassOrPackage> {
        @Override
        public ClassOrPackage deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            String str = json.getAsString();
            if (str.endsWith("*")) { // package case
                return new ClassOrPackage(str.substring(0, str.length() - 1));
            } else { // class case
                try {
                    return new ClassOrPackage(Utils.classFromString(str));
                } catch (ClassNotFoundException e) {
                    System.err.println("Error while deserializing json string to class: " + str);
//                    e.printStackTrace();
                    return null;
                }
            }
        }
    }

    private static class ExcludedClasses {
        List<ClassOrPackage> types;
        boolean matches(Class<?> c) {
            for (ClassOrPackage cop : types)
                if (cop != null && cop.matches(c))
                    return true;
            return false;
        }
    }
    private static class ExcludedConstructors {
        List<ClassOrPackage> types;
        Map<Integer, Class<?>> parameters;
        boolean matches(Constructor<?> c, Class<?> cls) {
            boolean matchedType = false;
            for (ClassOrPackage cop : types) {
                if (cop != null && cop.matches(cls)) {
                    matchedType = true;
                    break;
                }
            }
            if (!matchedType)
                return false;
            if (parameters != null) {
                for (int i : parameters.keySet()) {
                    Class<?> expected = parameters.get(i);
                    if (c.getParameterCount() <= i || !c.getParameterTypes()[i].equals(expected))
                        return false;
                }
            }
            return true;
        }
    }
    private static class ExcludedMethods {
        ClassOrPackage type;
        List<String> names;
        Map<Integer, Class<?>> parameters;
        boolean matches(Method m, Class<?> cls) {
            if (type != null && !type.matches(cls))
                return false;
            boolean matchedName = false;
            for (String name : names) {
                boolean thisMatch = false;
                if (name.endsWith("*"))
                    thisMatch = m.getName().startsWith(name.substring(0, name.length() - 1));
                else if (name.startsWith("*"))
                    thisMatch = m.getName().endsWith(name.substring(1));
                else
                    thisMatch = m.getName().equals(name);
                if (thisMatch) {
                    matchedName = true;
                    break;
                }
            }
            if (!matchedName)
                return false;
            if (parameters != null) {
                for (int i : parameters.keySet()) {
                    Class<?> expected = parameters.get(i);
                    if (m.getParameterCount() <= i || !m.getParameterTypes()[i].equals(expected))
                        return false;
                }
            }
            return true;
        }
    }

    private List<ExcludedClasses> classes = new ArrayList<>();
    private List<ExcludedConstructors> constructors = new ArrayList<>();
    private List<ExcludedMethods> methods = new ArrayList<>();
}

