// Contains useful helper functions.

package frangel.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import frangel.JavaFunctionLoader;
import frangel.Settings;
import frangel.SynthesisTask;
import frangel.model.FunctionData;
import frangel.model.expression.*;

public class Utils {
    private static Random rand = new Random();
    static {
//        rand.setSeed(123);
    }

    public static String stringFromFile(String filename) {
        try {
            return new String(Files.readAllBytes(Paths.get(filename)));
        } catch (IOException e) {
            return null;
        }
    }

    public static boolean stringToFile(String str, String filename) {
        try {
            PrintWriter out = new PrintWriter(new FileWriter(filename));
            out.print(str);
            out.close();
        } catch (Exception e) {
            System.err.println("Error writing to file " + filename);
            return false;
        }
        return true;
    }

    public static Class<?> classFromString(String s) throws ClassNotFoundException {
        if (s.endsWith("[]")) {
            String transformed = s;
            while (transformed.endsWith("[]"))
                transformed = "[" + transformed.substring(0, transformed.length() - 2);
            String[] from = new String[] {"boolean", "byte", "char", "double", "float", "int", "long", "short"};
            String[] to = new String[] {"Z", "B", "C", "D", "F", "I", "J", "S"};
            boolean primitiveArray = false;
            for (int i = 0; i < from.length; i++) {
                if (transformed.replace("[", "").equals(from[i])) {
                    transformed = transformed.replace(from[i], to[i]);
                    primitiveArray = true;
                    break;
                }
            }
            if (!primitiveArray) {
                int lastBracket = transformed.lastIndexOf('[');
                transformed = transformed.substring(0, lastBracket + 1) + "L" + transformed.substring(lastBracket + 1) + ";";
            }
            return Class.forName(transformed);
        }

        switch (s) {
        case "int":
            return int.class;
        case "long":
            return long.class;
        case "short":
            return short.class;
        case "byte":
            return byte.class;
        case "boolean":
            return boolean.class;
        case "char":
            return char.class;
        case "double":
            return double.class;
        case "float":
            return float.class;
        case "void":
            return void.class;
        default:
            return Class.forName(s);
        }
    }

    // returned set includes the given class
    public static Set<Class<?>> getSuperTypes(Class<?> cls) {
        Queue<Class<?>> q = new ArrayDeque<Class<?>>();
        q.add(cls);
        Set<Class<?>> superTypes = new HashSet<Class<?>>();
        superTypes.add(cls);
        while (!q.isEmpty()) {
            Class<?> cur = q.poll();
            Class<?> superclass = cur.getSuperclass();
            if (superclass != null && !superTypes.contains(superclass)) {
                superTypes.add(superclass);
                q.add(superclass);
            }
            for (Class<?> inter : cur.getInterfaces()) {
                if (!superTypes.contains(inter)) {
                    superTypes.add(inter);
                    q.add(inter);
                }
            }
        }
        if (!cls.equals(void.class) && !cls.isPrimitive())
            superTypes.add(Object.class); // Necessary if cls is an interface
        return superTypes;
    }

    public static String indent(int amount) {
        return indent(amount, Settings.INDENT);
    }

    public static String indent(int amount, String indentStr) {
        return String.join("", Collections.nCopies(amount, indentStr));
    }

    public static <E extends Number> double mean(List<E> list) {
        double sum = 0.0;
        for (Number n : list)
            sum += n.doubleValue();
        return sum / list.size();
    }

    public static <E extends Number> double median(List<E> list) {
        List<Double> copy = new ArrayList<>();
        for (E e : list)
            copy.add(e.doubleValue());
        Collections.sort(copy);
        return (copy.get(copy.size() / 2) + copy.get((copy.size() - 1) / 2)) / 2;
    }

    public static <T> T randElement(T[] arr) {
        return arr[rand.nextInt(arr.length)];
    }

    public static <T> T randElement(List<T> list) {
        return list.get(rand.nextInt(list.size()));
    }

    public static <T> void shuffle(T[] arr) {
        for (int i = arr.length - 1; i > 0; i--) {
            int j = rand.nextInt(i + 1);
            T temp = arr[j];
            arr[j] = arr[i];
            arr[i] = temp;
        }
    }

    public static <T> void shuffle(List<T> list) {
        Collections.shuffle(list, rand);
    }

    public static boolean randBoolean() {
        return rand.nextBoolean();
    }

    public static boolean randBoolean(double prob) {
        return randDouble() < prob;
    }

    public static int randInt(int max) {
        return rand.nextInt(max);
    }

    // In the range [min, max)
    public static int randInt(int min, int max) {
        return min + rand.nextInt(max - min);
    }

    public static double randDouble() {
        return rand.nextDouble();
    }

    // Partitions "total" elements into a given number of "parts", each part having at least "min" elements
    // Null if impossible (e.g., parts * min > total)
    // Ex: randPartition(10, 3, 2) might return [2, 5, 3]
    public static int[] randPartition(int total, int parts, int min) {
        total -= parts * min;
        if (total < 0)
            return null;

        // Randomly place bars that separate the parts
        int[] ans = new int[parts];
        ans[0] = total;
        for (int i = 1; i < parts; i++)
            ans[i] = rand.nextInt(total + 1);
        Arrays.sort(ans);

        int start = 0;
        for (int i = 0; i < parts; i++) {
            int end = ans[i];
            ans[i] = min + end - start;
            start = end;
        }
        return ans;
    }

    public static double timeSince(long start) {
        return (System.nanoTime() - start) / 1.0e9;
    }

    public static long getTimeout(double seconds) {
        return getTimeout(System.nanoTime(), seconds);
    }

    public static long getTimeout(long start, double seconds) {
        return start + (long)(1.0e9 * seconds);
    }

    public static boolean timeout(long timeout) {
        return System.nanoTime() > timeout;
    }

    public static int numComponents(SynthesisTask task) {
        return JavaFunctionLoader.numData() + (Settings.SYPET_MODE ? 0 : OpExpression.numOp() + task.numLiterals());
    }

    private static String escape(String s) {
        return s.replace("\\", "\\\\").replace("\n", "\\n").replace("\r", "\\r").replace("\"", "\\\"").replace("'", "\\'");
    }

    public static String toStringLiteral(String s) {
        return "\"" + escape(s) + "\"";
    }

    public static String toCharLiteral(char c) {
        return toCharLiteral(c + "");
    }

    public static String toCharLiteral(String c) {
        return "'" + escape(c) + "'";
    }

    public static Class<?> getParameterTypeForClass(Class<?> cls, Map<Class<?>, Class<?>> parameterTypeMap) {
        if (parameterTypeMap == null || cls.getTypeParameters().length == 0)
            return null;
        Class<?> ans = parameterTypeMap.get(cls);
        if (ans != null)
            return ans;
        for (Class<?> key : parameterTypeMap.keySet())
            if (key.isAssignableFrom(cls))
                return parameterTypeMap.get(key);
        return Object.class;
    }

    public static String getParameterizedName(Class<?> container, Class<?> parameterType) {
        return getParameterizedName(container, parameterType, Settings.USE_SIMPLE_NAME);
    }

    public static String getParameterizedName(Class<?> container, Class<?> parameterType, boolean simple) {
        if (parameterType == null) {
            return getClassName(container, simple);
        } else {
            String ans = getClassName(container, simple) + "<";
            for (int i = 0; i < container.getTypeParameters().length; i++)
                ans += (i == 0 ? "" : ", ") + getClassName(parameterType, simple);
            ans += ">";
            return ans;
        }
    }

    public static String getParameterizedName(Class<?> container, Map<Class<?>, Class<?>> parameterTypeMap) {
        return getParameterizedName(container, getParameterTypeForClass(container, parameterTypeMap));
    }

    public static String getClassName(Class<?> cls) {
        return getClassName(cls, Settings.USE_SIMPLE_NAME);
    }
    public static String getClassName(Class<?> cls, boolean simple) {
        if (simple) {
            String ans = cls.getSimpleName();
            Class<?> enclosing = cls.getEnclosingClass();
            while (enclosing != null) {
                ans = enclosing.getSimpleName() + "." + ans;
                enclosing = enclosing.getEnclosingClass();
            }
            return ans;
        } else {
            String name = cls.getCanonicalName();
            if (name.startsWith("java.lang."))
                name = name.substring("java.lang.".length());
            return name;
        }
    }

    public static String getTypeString(Object o) {
        if (o == null)
            return "null";
        return o.getClass().getCanonicalName();
    }

    public static boolean typeMatch(Class<?> cls, Object o) {
        if (cls.equals(void.class))
            return o == null;
        if (o == null)
            return !cls.isPrimitive();
        if (cls.isAssignableFrom(o.getClass()))
            return true;
        if (cls.isPrimitive()) {
            Class<?> oClass = o.getClass();
            if (cls.equals(int.class)) {
                return Integer.class.isAssignableFrom(oClass);
            } else if (cls.equals(long.class)) {
                return Long.class.isAssignableFrom(oClass) || Integer.class.isAssignableFrom(oClass);
            } else if (cls.equals(char.class)) {
                return Character.class.isAssignableFrom(oClass);
            } else if (cls.equals(boolean.class)) {
                return Boolean.class.isAssignableFrom(oClass);
            } else if (cls.equals(double.class)) {
                return Double.class.isAssignableFrom(oClass);
            } else if (cls.equals(float.class)) {
                return Float.class.isAssignableFrom(oClass) || Double.class.isAssignableFrom(oClass);
            } else if (cls.equals(short.class)) {
                return Short.class.isAssignableFrom(oClass);
            } else if (cls.equals(byte.class)) {
                return Byte.class.isAssignableFrom(oClass);
            }
        }
        return false;
    }

    public static Expression getInitialValueForType(Class<?> type, SynthesisTask task) {
        Expression initialValue = Utils.getDefaultLiteral(type, task);
        if (initialValue == null)
            initialValue = Utils.getDefaultValue(type, task);
        return initialValue;
    }

    private static LiteralExpression getDefaultLiteral(Class<?> cls, SynthesisTask task) {
        Map<Class<?>, List<Object>> literals = task.getLiterals();
        if (!literals.containsKey(cls))
            return null;
        return new LiteralExpression(literals.get(cls).get(0), cls);
    }

    private static Expression getDefaultValue(Class<?> cls, SynthesisTask task) {
        if (cls.isPrimitive()) {
            if (cls.equals(double.class) || cls.equals(float.class))
                return new LiteralExpression(0.0, cls);
            if (cls.equals(boolean.class))
                return new LiteralExpression(false, cls);
            return new LiteralExpression(0, cls); // int, long, short, byte, char
        } else {
            for (Constructor<?> c : cls.getConstructors()) {
                if (Modifier.isPublic(c.getModifiers()) && c.getParameterCount() == 0) {
                    FunctionData data = new FunctionData(c, Utils.getParameterTypeForClass(cls, task.getParameterTypeMap()));
                    return new FuncExpression(new Expression[0], null, data);
                }
            }
            return new LiteralExpression(null, cls);
        }
    }

    public static <T> void replaceIfPresent(Set<T> set, T oldValue, T newValue) {
        if (set.contains(oldValue)) {
            set.remove(oldValue);
            set.add(newValue);
        }
    }

    public static <K, V> void replaceKeyIfPresent(Map<K, V> map, K oldKey, K newKey) {
        if (map.containsKey(oldKey)) {
            V value = map.get(oldKey);
            map.remove(oldKey);
            map.put(newKey, value);
        }
    }
}
