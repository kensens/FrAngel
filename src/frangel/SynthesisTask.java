package frangel;

import java.io.IOException;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;

import frangel.model.FunctionData;
import frangel.utils.Utils;

public class SynthesisTask {
    static ImmutableSet<ClassInfo> ALL_CLASSES;
    static {
        try {
            ALL_CLASSES = ClassPath.from(Thread.currentThread().getContextClassLoader()).getAllClasses();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String name;
    private int numInputs;
    private Class<?>[] inputTypes;
    private String[] inputNames;
    private boolean inputsMutable;
    private Class<?> outputType;
    private Set<Class<?>> classes;
    private String[] packages;
    private Map<Class<?>, List<Object>> literals;
    private Map<Class<?>, Class<?>> parameterTypeMap;
    private List<Example> examples;
    private Map<Class<?>, BiPredicate<Object, Object>> equalityTesters;
    private Map<Class<?>, Function<Object, String>> toStrings;
    private Set<Method> excludedMethods;
    private Class<?> declaringClass;
    private List<Tag> tags;

    private boolean finalized;
    private String group;

    public SynthesisTask() {
        name = "synthesisTask";
        numInputs = 0;
        inputTypes = new Class<?>[0];
        inputNames = new String[0];
        inputsMutable = true;
        outputType = void.class;
        classes = new HashSet<>();
        packages = new String[0];
        literals = new HashMap<>();
        parameterTypeMap = new HashMap<>();
        examples = new ArrayList<>();
        equalityTesters = new HashMap<>();
        toStrings = new HashMap<>();
        excludedMethods = new HashSet<>();
        declaringClass = null;
        tags = new ArrayList<>();

        finalized = false;
        group = null;

        // Default literals
        literals.put(int.class, new ArrayList<>(Arrays.asList(0, 1, 2, -1)));
        literals.put(long.class, new ArrayList<>(Arrays.asList(0L, 1L, 2L)));
        literals.put(double.class, new ArrayList<>(Arrays.asList(0.0, 1.0, 2.0)));
        literals.put(float.class, new ArrayList<>(Arrays.asList(0.0f, 1.0f, 2.0f)));
        literals.put(boolean.class, new ArrayList<>(Arrays.asList(true, false)));
        literals.put(String.class, new ArrayList<>(Arrays.asList("", null)));
        literals.put(Object.class, new ArrayList<>(Arrays.asList((Object) null)));
    }

    private void checkJavaIdentifier(String name) {
        if (name == null || name.isEmpty())
            throw new SynthesisTaskException("The name cannot be null or empty");
        if (!Character.isJavaIdentifierStart(name.charAt(0)))
            throw new SynthesisTaskException("The name \"" + name + "\" starts with an illegal character '" + name.charAt(0) + "'");
        for (char c : name.toCharArray())
            if (!Character.isJavaIdentifierPart(c))
                throw new SynthesisTaskException("The name \"" + name + "\" contains an illegal character '" + c + "'");
    }

    public SynthesisTask setName(String name) {
        checkJavaIdentifier(name);
        this.name = name;
        return this;
    }

    public SynthesisTask setInputTypes(Class<?>... types) {
        if (types == null) {
            numInputs = 0;
            inputTypes = new Class<?>[0];
        } else {
            for (Class<?> cls : types)
                if (cls == null || cls.equals(void.class))
                    throw new SynthesisTaskException("Input types cannot be null or void");
            numInputs = types.length;
            inputTypes = Arrays.copyOf(types, numInputs);
        }
        return this;
    }

    private void checkNameClashes(String name) {
        String rest = "";
        for (String prefix : new String[] {"arg", "var", "i", "elem"}) {
            if (name.startsWith(prefix)) {
                rest = name.substring(prefix.length());
                break;
            }
        }
        try {
            Integer.parseInt(rest);
            throw new SynthesisTaskException("Input names cannot be \"arg\", \"var\", \"i\", or \"elem\" followed by an integer (reserved for use by synthesizer)");
        } catch (NumberFormatException e) {
        }
    }

    public SynthesisTask setInputNames(String... names) {
        if (names == null) {
            inputNames = new String[0];
        } else {
            for (String name : names) {
                checkJavaIdentifier(name);
                checkNameClashes(name);
            }
            inputNames = Arrays.copyOf(names, names.length);
            if (new HashSet<String>(Arrays.asList(inputNames)).size() != inputNames.length)
                throw new SynthesisTaskException("Duplicate input names");
        }
        return this;
    }

    public SynthesisTask setOutputType(Class<?> type) {
        if (type == null)
            type = void.class;
        outputType = type;
        return this;
    }

    public SynthesisTask addClasses(Class<?>... classes) {
        if (classes == null)
            this.classes.clear();
        else
            this.classes.addAll(Arrays.asList(classes));
        return this;
    }

    public SynthesisTask addPackages(String... packages) {
        if (packages == null)
            this.packages = new String[0];
        else
            this.packages = Arrays.copyOf(packages, packages.length);
        return this;
    }

    public SynthesisTask addLiterals(Class<?> type, Object... literals) {
        if (type == null || type.equals(void.class) || !(type.isPrimitive() || type.equals(String.class)))
            throw new SynthesisTaskException("Only primitive and String literals are supported");
        if (literals == null)
            return this;
        for (Object literal : literals) {
            // TODO: check if literal has the right type? convert literal=Integer to literal=Long when type=long, etc.?
            if (!this.literals.containsKey(type))
                this.literals.put(type, new ArrayList<>());
            this.literals.get(type).add(literal);
        }
        if (type.equals(char.class)) { // also add String literals for char literals (indexOf takes int/String but not char)
            for (Object literal : literals)
                addLiterals(String.class, "" + literal);
        }
        return this;
    }

    public SynthesisTask clearLiterals() {
        literals.clear();
        return this;
    }

    public SynthesisTask addGenerics(Class<?> container, Class<?> containedType) {
        if (container == null || container.equals(void.class) || containedType == null || containedType.equals(void.class))
            throw new SynthesisTaskException("The types cannot be null or void");
        if (container.getTypeParameters().length == 0)
            throw new SynthesisTaskException("The type " + container.getCanonicalName() + " does not use type parameters");
        if (containedType.isPrimitive())
            throw new SynthesisTaskException("Generic type parameters cannot be primitive types");
        parameterTypeMap.put(container, containedType);
        return this;
    }

    public SynthesisTask clearGenerics() {
        parameterTypeMap.clear();
        return this;
    }

    public SynthesisTask addExample(Example example) {
        examples.add(example);
        return this;
    }

    public SynthesisTask addExamples(Example... examples) {
        if (examples != null)
            for (Example e : examples)
                this.examples.add(e);
        return this;
    }

    public SynthesisTask clearExamples() {
        examples.clear();
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> SynthesisTask addEqualityTester(Class<?> type, BiPredicate<T, T> equalityTester) {
        equalityTesters.put(type, (Object a, Object b) -> (a == null || b == null) ? a == b : equalityTester.test((T) a, (T) b));
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> SynthesisTask addToString(Class<?> type, Function<T, String> toStringFunc) {
        toStrings.put(type, (Object x) -> (x == null) ? "null" : toStringFunc.apply((T) x));
        return this;
    }

    public SynthesisTask makeInputsImmutable() {
        inputsMutable = false;
        return this;
    }
    public SynthesisTask makeInputsMutable(boolean mutable) {
        inputsMutable = mutable;
        return this;
    }

    public SynthesisTask excludeMethods(Method... excludedMethods) {
        if (excludedMethods == null)
            this.excludedMethods.clear();
        else
            this.excludedMethods.addAll(Arrays.asList(excludedMethods));
        return this;
    }

    public SynthesisTask setDeclaringClass(Class<?> declaringClass) {
        this.declaringClass = declaringClass;
        return this;
    }

    public SynthesisTask setGroup(String group) {
        this.group = group;
        return this;
    }

    public SynthesisTask addTags(Tag... tags) {
        this.tags.addAll(Arrays.asList(tags));
        return this;
    }

    public void finalizeSetup() {
        if (!finalized) {
            linkWithExamples();
            sanityCheck();
            inferRelevantTypes();
        }
        finalized = true;
    }

    private void linkWithExamples() {
        for (int i = 0; i < examples.size(); i++)
            examples.get(i).setTask(this, i);
    }

    private void sanityCheck() {
        if (inputNames.length > numInputs)
            throw new SynthesisTaskException(inputNames.length + " input names provided (" + numInputs + " expected)");
        if (examples.isEmpty())
            throw new SynthesisTaskException("No examples provided");
        for (int i = 0; i < examples.size(); i++) {
            Example e = examples.get(i);
            e.sanityCheck(i);
        }
    }

    private void inferRelevantTypes() {
        Queue<Class<?>> q = new ArrayDeque<>();
        q.addAll(Arrays.asList(inputTypes));
        q.add(outputType);
        if (declaringClass != null)
            q.add(declaringClass);
        if (parameterTypeMap != null)
            q.addAll(parameterTypeMap.values());
        if (parameterTypeMap == null)
            parameterTypeMap = new HashMap<>();
        for (Class<?> cls : new Class<?>[] {List.class, Set.class, Map.class, Queue.class})
            if (!parameterTypeMap.containsKey(cls))
                parameterTypeMap.put(cls, Object.class);
        Set<Class<?>> relevantClasses = new HashSet<Class<?>>();
        while (!q.isEmpty()) {
            Class<?> cls = q.poll();
            if (cls.equals(void.class))
                continue;
            if (Collection.class.isAssignableFrom(cls))
                relevantClasses.add(Object.class);
            relevantClasses.add(cls);
            for (Class<?> inner : cls.getDeclaredClasses())
                if (Modifier.isPublic(inner.getModifiers()))
                    q.add(inner);
            if (cls.isArray())
                q.add(cls.getComponentType());
        }

        for (Class<?> cls : relevantClasses)
            if (!Excluded.isClassExcluded(cls))
                classes.add(cls);

        for (String p : packages) {
            for (final ClassInfo info : ALL_CLASSES) {
                if (p.endsWith("*"))
                    p = p.substring(0, p.length() - 1);
                if (info.getName().startsWith(p)) {
                    try {
                        Class<?> cls = info.load();
                        if (!Throwable.class.isAssignableFrom(cls) && Modifier.isPublic(cls.getModifiers()) && !Excluded.isClassExcluded(cls))
                            classes.add(cls);
                    } catch (NoClassDefFoundError | TypeNotPresentException | SecurityException e) {
                        // e.printStackTrace();
                    }
                }
            }
        }

        // remove bad classes
        for (Class<?> cls : new ArrayList<>(classes)) {
            if (cls.isAnnotationPresent(Deprecated.class)) {
                classes.remove(cls);
                continue;
            }
            try {
                Class.forName(cls.getName());
                Class<?> parameterType = Utils.getParameterTypeForClass(cls, parameterTypeMap);
                for (Method m : cls.getMethods()) {
                    if (Modifier.isPublic(m.getModifiers())) {
                        m.setAccessible(true);
                        new FunctionData(m, parameterType);
                    }
                }
                if (!Modifier.isAbstract(cls.getModifiers())) {
                    for (Constructor<?> con : cls.getConstructors()) {
                        if (Modifier.isPublic(con.getModifiers())) {
                            con.setAccessible(true);
                            if (con.getParameterCount() == 0)
                                con.newInstance(new Object[0]);
                        }
                    }
                }
            } catch (NoClassDefFoundError | Exception e) {
                classes.remove(cls);
            }
        }

        classes.add(int.class); // generally useful
        classes.remove(void.class);
    }

    protected String objectToString(Object obj) {
        if (obj == null)
            return "null";

        Class<?> type = obj.getClass();

        if (toStrings.containsKey(type))
            return toStrings.get(type).apply(obj);

        for (Class<?> cls : toStrings.keySet())
            if (cls.isAssignableFrom(type))
                return toStrings.get(cls).apply(obj);

        if (type.isArray()) {
            StringBuilder sb = new StringBuilder("[");
            int length = Array.getLength(obj);
            String sep = "";
            for (int i = 0; i < length; i++) {
                sb.append(sep).append(objectToString(Array.get(obj, i)));
                sep = ", ";
            }
            sb.append("]");
            return sb.toString();
        }

        if (Map.class.isAssignableFrom(type)) {
            StringBuilder sb = new StringBuilder(type.getSimpleName()).append('{');
            String sep = "";
            Map<?, ?> map = (Map<?, ?>) obj;
            for (Object key : map.keySet()) {
                Object value = map.get(key);
                sb.append(sep).append(objectToString(key)).append(": ").append(objectToString(value));
                sep = ", ";
            }
            sb.append('}');
            return sb.toString();
        }

        if (Collection.class.isAssignableFrom(type)) {
            String ends = "[]";
            if (Set.class.isAssignableFrom(type))
                ends = "{}";
            StringBuilder sb = new StringBuilder(type.getSimpleName()).append(ends.charAt(0));
            String sep = "";
            Collection<?> coll = (Collection<?>) obj;
            for (Object o : coll) {
                sb.append(sep).append(objectToString(o));
                sep = ", ";
            }
            sb.append(ends.charAt(1));
            return sb.toString();
        }

        return obj.toString();
    }

    private String classesToString(Iterable<Class<?>> it) {
        String s = "[";
        String sep = "";
        for (Class<?> c : it) {
            s += sep + c.getCanonicalName();
            sep = ", ";
        }
        s += "]";
        return s;
    }
    private <T> String classMapToString(Map<Class<?>, T> map) {
        String s = "[";
        String sep = "";
        for (Class<?> c : map.keySet()) {
            s += sep + c.getCanonicalName() + "=" + map.get(c).toString();
            sep = ", ";
        }
        s += "]";
        return s;
    }
    @Override
    public String toString() {
        String ans = "name: " + name + "\n";
        ans += "inputTypes: " + classesToString(Arrays.asList(inputTypes)) + "\n";
        ans += "outputType: " + outputType.getCanonicalName() + "\n";
        ans += "libraries: (" + classes.size() + ") " + classesToString(classes) + "\n";
        ans += "literals: " + classMapToString(literals) + "\n";
        return ans;
    }

    public String getName() {
        return name;
    }
    public int getNumInputs() {
        return numInputs;
    }
    public Class<?>[] getInputTypes() {
        return inputTypes;
    }
    public String[] getInputNames() {
        return inputNames;
    }
    public String getInputName(int index) {
        if (index >= inputNames.length)
            return "arg" + (index + 1);
        return inputNames[index];
    }
    public Class<?> getOutputType() {
        return outputType;
    }
    public boolean returns() {
        return !outputType.equals(void.class);
    }
    public Set<Class<?>> getClasses() {
        return classes;
    }
    private Class<?>[] classesCachedArray;
    public Class<?>[] getClassesCachedArray() {
        if (classesCachedArray == null)
            classesCachedArray = classes.toArray(new Class<?>[classes.size()]);
        return classesCachedArray;
    }
    public String[] getPackages() {
        return packages;
    }
    public Map<Class<?>, List<Object>> getLiterals() {
        return literals;
    }
    public List<Object> getLiteralsForType(Class<?> type) {
        return literals.get(type);
    }
    public int numLiterals() {
        int num = 0;
        for (List<Object> list : literals.values())
            num += list.size();
        return num;
    }
    public List<Example> getExamples() {
        return examples;
    }
    public Example getExample(int i) {
        return examples.get(i);
    }
    public int numExamples() {
        return examples.size();
    }
    public BiPredicate<Object, Object> getEqualityTester(Class<?> type) {
        return equalityTesters.get(type);
    }
    public Map<Class<?>, BiPredicate<Object, Object>> getEqualityTesters() {
        return equalityTesters;
    }
    public boolean inputsMutable() {
        return inputsMutable;
    }
    public Map<Class<?>, Class<?>> getParameterTypeMap() {
        return parameterTypeMap;
    }
    public Set<Method> getExcludedMethods() {
        return excludedMethods;
    }
    public Class<?> getDeclaringClass() {
        return declaringClass;
    }
    public String getGroup() {
        return group;
    }
    public List<Tag> getTags() {
        return tags;
    }
}
