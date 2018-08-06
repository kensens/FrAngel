// Loads Java functions.

package frangel;

import java.lang.reflect.*;
import java.util.*;

import frangel.model.FunctionData;
import frangel.model.FunctionData.Kind;
import frangel.utils.TimeLogger;
import frangel.utils.Utils;

public class JavaFunctionLoader {

    // Every version of an overloaded function corresponds to a separate FunctionData object
    private static List<FunctionData> allData; // includes methods, fields, constructors, and array operations
    private static List<FunctionData> allMethods; // only methods
    private static Map<Class<?>, List<FunctionData>> functionsByReturnType;

    private static Map<Class<?>, Set<Class<?>>> supers;
    private static Map<Method, Set<Class<?>>> possibleCallingTypes; // if A is assignable from B, then only include A

    private static FunctionData keySetData = null;

    private static Set<Class<?>> getArrayClasses(SynthesisTask task, Set<Class<?>> arrayTypes) {
        Set<Class<?>> relevant = new HashSet<Class<?>>(arrayTypes);
        relevant.addAll(task.getClasses());

        Set<Class<?>> ans = new HashSet<Class<?>>();
        for (Class<?> cls : relevant) {
            while (cls.isArray()) {
                ans.add(cls);
                cls = cls.getComponentType();
            }
        }
        return ans;
    }

    // Loads methods from library classes
    public static void resetData(SynthesisTask task) {
        TimeLogger.start("JavaFunction.resetData()");
        FunctionData.resetEncodingMap();
        allData = new ArrayList<>();
        supers = new HashMap<>();
        possibleCallingTypes = new HashMap<>();

        Set<Class<?>> returnedArrayTypes = new HashSet<Class<?>>();

        try {
            for (Class<?> c : task.getClasses()) {
                if (Excluded.isClassExcluded(c))
                    continue;
                boolean declaringClass = c.equals(task.getDeclaringClass());

                supers.put(c, Utils.getSuperTypes(c));

                Class<?> parameterType = Utils.getParameterTypeForClass(c, task.getParameterTypeMap());

                Set<Method> methods = new HashSet<>(Arrays.asList(c.getMethods()));
                if (declaringClass)
                    methods.addAll(Arrays.asList(c.getDeclaredMethods()));
                for (Method m : methods) {
                    if (m.isAnnotationPresent(Deprecated.class))
                        continue; // don't use deprecated methods
                    if (task.getExcludedMethods().contains(m) || Excluded.isMethodExcluded(m, c))
                        continue;
                    if (Modifier.isStatic(m.getModifiers()) && m.getName().equals("main"))
                        continue; // don't run static main methods
                    if (!declaringClass && !Modifier.isPublic(m.getModifiers()))
                        continue; // ensure methods are visible

                    m.setAccessible(true);
                    FunctionData data = new FunctionData(m, parameterType);
                    if (data.isValid()) {
                        allData.add(data);
                        if (!data.isStatic())
                            possibleCallingTypes.put(m, getCallerTypes(m, supers.get(c)));
                        if (data.returns() && data.getReturnType().isArray())
                            returnedArrayTypes.add(data.getReturnType());
                    }
                    if (Map.class.isAssignableFrom(c) && m.getName().equals("keySet") && m.getParameterTypes().length == 0)
                        keySetData = data;
                }

                if (!Modifier.isAbstract(c.getModifiers())) {
                    for (Constructor<?> con : c.getConstructors()) {
                        if (Excluded.isConstructorExcluded(con, c))
                            continue;
                        if (!declaringClass && !Modifier.isPublic(con.getModifiers()))
                            continue;

                        con.setAccessible(true);
                        FunctionData data = new FunctionData(con, parameterType);
                        if (data.isValid())
                            allData.add(data);
                    }
                }

                if (!Settings.SYPET_MODE) {
                    Set<Field> fields = new HashSet<>(Arrays.asList(c.getFields()));
                    if (declaringClass)
                        fields.addAll(Arrays.asList(c.getDeclaredFields()));
                    for (Field f : fields) {
                        if (!Settings.USE_CLASS_CONSTANTS && (Modifier.isStatic(f.getModifiers()) || Modifier.isFinal(f.getModifiers())))
                            continue;
                        // public non-static fields (instance variables), or public final (static) fields (constants)
                        if ((declaringClass || Modifier.isPublic(f.getModifiers())) &&
                                (!Modifier.isStatic(f.getModifiers()) || Modifier.isFinal(f.getModifiers()))) {
                            f.setAccessible(true);
                            FunctionData data = new FunctionData(f, parameterType);
                            if (data.isValid())
                                allData.add(data);
                            if (data.returns() && data.getReturnType().isArray())
                                returnedArrayTypes.add(data.getReturnType());
                        }
                    }
                }
            }

            for (Class<?> cls : getArrayClasses(task, returnedArrayTypes)) {
                Class<?> compCls = cls.getComponentType();
                allData.add(new FunctionData(compCls, new Class<?>[] {cls, int.class}, Kind.ARR_GET));
                allData.add(new FunctionData(void.class, new Class<?>[] {cls, int.class, compCls}, Kind.ARR_SET));
                allData.add(new FunctionData(int.class, new Class<?>[] {cls}, Kind.ARR_LEN));
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("(from JavaFunctionLoader)");
        }

        functionsByReturnType = new HashMap<Class<?>, List<FunctionData>>();
        for (FunctionData data : allData) {
            Class<?> returnType = data.getReturnType();
            Set<Class<?>> returnSuperTypes = supers.get(returnType);
            if (returnSuperTypes == null) {
                returnSuperTypes = Utils.getSuperTypes(returnType);
                supers.put(returnType, returnSuperTypes);
            }
            for (Class<?> superType : returnSuperTypes) {
                if (Settings.SYPET_MODE && Settings.HARDCODE_POLYMORPHISM && !superType.equals(returnType) &&
                        Settings.POLYMORPHISM_MAP.containsKey(superType) && !Settings.POLYMORPHISM_MAP.get(superType).contains(returnType))
                    continue;
                addFunctionByReturnType(superType, data);
            }
        }

        allMethods = new ArrayList<FunctionData>();
        for (FunctionData d : allData)
            if (d.getKind() == FunctionData.Kind.METHOD)
                allMethods.add(d);

        // Sort all lists (for reproducibility)
        Collections.sort(allData);
        Collections.sort(allMethods);
        for (Class<?> cls : functionsByReturnType.keySet())
            Collections.sort(functionsByReturnType.get(cls));

        TimeLogger.stop("JavaFunction.resetData()");
    }

    private static void addFunctionByReturnType(Class<?> type, FunctionData data) {
        if (!functionsByReturnType.containsKey(type))
            functionsByReturnType.put(type, new ArrayList<FunctionData>());
//        if (!functionsByReturnType.get(type).contains(data))
        functionsByReturnType.get(type).add(data);
    }

    private static Set<Class<?>> getCallerTypes(Method m, Set<Class<?>> superTypes) {
        Set<Class<?>> callerTypes = new HashSet<>();
        for (Class<?> s : superTypes) {
            try {
                Method m2 = s.getMethod(m.getName(), m.getParameterTypes());
                if (m.getReturnType().isAssignableFrom(m2.getReturnType()))
                    callerTypes.add(s);
            } catch (Exception e) {
                // method not declared in superclass, ignore
            }
        }
        for (Class<?> c : new ArrayList<>(callerTypes)) {
            if (!callerTypes.contains(c))
                continue; // c was already removed
            for (Class<?> other : new ArrayList<>(callerTypes))
                if (other != c && c.isAssignableFrom(other))
                    callerTypes.remove(other);
        }
        return callerTypes;
    }

    public static List<FunctionData> getFunctionsByReturnType(Class<?> type) {
        return functionsByReturnType.get(type);
    }

    // Excludes constructors, fields, and array operations
    public static List<FunctionData> getAllMethods() {
        return allMethods;
    }

    public static int numData() {
        return allData.size();
    }

    public static List<FunctionData> getCallableVoidMethods(Set<Class<?>> callableClasses) {
        List<FunctionData> voidCallable = new ArrayList<>();
        List<FunctionData> voidMethods = getFunctionsByReturnType(void.class);
        if (voidMethods == null)
            return null;
        for (FunctionData data : voidMethods) {
            if (data.isStatic())
                continue;
            if (callableClasses.contains(data.getCallerClass()))
                voidCallable.add(data);
        }
        return voidCallable;
    }

    public static Set<Class<?>> getSuperTypes(Class<?> cls) {
        if (supers != null) {
            Set<Class<?>> ans = supers.get(cls);
            if (ans != null)
                return ans;
        }
        return Utils.getSuperTypes(cls);
    }

    public static Set<Class<?>> getPossibleCallingTypes(Method m) {
        return possibleCallingTypes.get(m);
    }

    public static FunctionData getKeySetData() {
        return keySetData;
    }
}
