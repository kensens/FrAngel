package frangel.model;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import frangel.utils.Utils;

public class FunctionData implements Comparable<FunctionData> {
    public enum Kind {METHOD, CONSTRUCTOR, FIELD, ARR_GET, ARR_SET, ARR_LEN};

    private String name;
    private String simpleName;
    private Class<?> callerClass;
    private Class<?> returnType; // void if doesn't return
    private Class<?>[] argTypes;
    private boolean isStatic;
    private boolean returns;
    private Kind kind;

    private Method method = null;
    private Constructor<?> constructor = null;
    private Field field = null;

    private static Map<String, Integer> encodingMap = new HashMap<String, Integer>();
    private String encoding;

    private boolean valid = true;
    private boolean returnsGeneric = false;

    private Class<?> convertGenericType(Type t, Class<?> nonGeneric, Class<?> parameterType) {
        if (t instanceof TypeVariable) {
            if (parameterType != null)
                return parameterType;
            return nonGeneric;
        } else if (parameterType != null && !(t instanceof Class)) {
            // t has something to do with generics, but is not just a TypeVariable
            valid = false;
        }
        return nonGeneric;
    }

    private Class<?>[] convertGenericTypes(Type[] arr, Class<?>[] nonGenericArr, Class<?> parameterType) {
        Class<?>[] converted = new Class<?>[arr.length];
        for (int i = 0; i < converted.length; i++)
            converted[i] = convertGenericType(arr[i], nonGenericArr[i], parameterType);
        return converted;
    }

    public FunctionData(Method m, Class<?> parameterType) {
        method = m;
        callerClass = m.getDeclaringClass();
        name = m.getName();
        simpleName = name;
        Type generic = m.getGenericReturnType();
        returnType = convertGenericType(generic, m.getReturnType(), parameterType);
        if (name.equals("equals") && m.getParameterCount() == 1)
            argTypes = new Class<?>[] {this.callerClass};
            else
                argTypes = convertGenericTypes(m.getGenericParameterTypes(), m.getParameterTypes(), parameterType);
        isStatic = Modifier.isStatic(m.getModifiers());
        returns = !returnType.equals(void.class);
        if (returns)
            returnsGeneric = !(generic instanceof Class);
        kind = Kind.METHOD;
        setEncoding();
    }

    public FunctionData(Constructor<?> con, Class<?> parameterType) {
        constructor = con;
        callerClass = con.getDeclaringClass();
        name = Utils.getParameterizedName(callerClass, parameterType);
        simpleName = Utils.getParameterizedName(callerClass, parameterType, true);
        returnType = callerClass;
        argTypes = convertGenericTypes(con.getGenericParameterTypes(), con.getParameterTypes(), parameterType);
        isStatic = true; // don't need an existing object to call constructor
        returns = true;
        kind = Kind.CONSTRUCTOR;
        setEncoding();
    }

    public FunctionData(Field f, Class<?> parameterType) {
        field = f;
        callerClass = f.getDeclaringClass();
        name = f.getName();
        simpleName = name;
        Type generic = f.getGenericType();
        returnType = convertGenericType(generic, f.getType(), parameterType);
        returnsGeneric = !(generic instanceof Class);
        argTypes = new Class<?>[0];
        isStatic = Modifier.isStatic(f.getModifiers());
        returns = true;
        kind = Kind.FIELD;
        setEncoding();
    }

    // Used to create special static methods (e.g., array operations)
    public FunctionData(Class<?> returnType, Class<?>[] argTypes, Kind kind) {
        name = null;
        this.returnType = returnType;
        this.argTypes = argTypes;
        this.kind = kind;
        callerClass = null;
        isStatic = true;
        returns = !returnType.equals(void.class);
        setEncoding();
    }

    public static void resetEncodingMap() {
        encodingMap = new HashMap<String, Integer>();
    }

    private void setEncoding() {
        String encodingKey = kind + (callerClass == null ? "~" : callerClass.getCanonicalName()) + "-" + name + "-" + argTypes.length + "-" + isStatic;
        if (!encodingMap.containsKey(encodingKey))
            encodingMap.put(encodingKey, encodingMap.size());
        int i = encodingMap.get(encodingKey);
        encoding = Integer.toString(i, 36);
    }

    public void encode(StringBuilder sb) {
        sb.append(encoding);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        FunctionData other = (FunctionData) obj;
        if (kind != other.kind)
            return false;
        switch (kind) {
        case METHOD:
            if (!name.equals(other.name))
                return false;
            if (method == null)
                return other.method == null;
            else
                return method.equals(other.method);
        case CONSTRUCTOR:
            return constructor.equals(other.constructor);
        case FIELD:
            return field.equals(other.field);
        case ARR_GET:
        case ARR_SET:
        case ARR_LEN:
            return Arrays.equals(argTypes, other.argTypes);
        default:
            System.err.println("Unknown FunctionData.Kind in FunctionData.equals()");
            return false;
        }
    }

    @Override
    public int hashCode() {
        final int prime = 167;
        int result = 1;
        result = prime * result + kind.hashCode();
        result = prime * result + name.hashCode();
        result = prime * result + (method == null ? 0 : method.hashCode());
        result = prime * result + (constructor == null ? 0 : constructor.hashCode());
        result = prime * result + (field == null ? 0 : field.hashCode());
        return result;
    }

    public String getName() {
        return name;
    }

    public String getName(boolean simpleName) {
        return simpleName ? this.simpleName : name;
    }

    public Class<?> getCallerClass() {
        return callerClass;
    }

    public Class<?> getReturnType() {
        return returnType;
    }

    public Class<?>[] getArgTypes() {
        return argTypes;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public boolean returns() {
        return returns;
    }

    public Kind getKind() {
        return kind;
    }

    public Method getMethod() {
        return method;
    }

    public Constructor<?> getConstructor() {
        return constructor;
    }

    public Field getField() {
        return field;
    }

    public boolean isValid() {
        return valid;
    }

    public boolean returnsGeneric() {
        return returnsGeneric;
    }

    private String comparisonKey;
    private String getComparisonKey() {
        if (comparisonKey != null)
            return comparisonKey;
        comparisonKey = (callerClass == null ? "null" : callerClass.getName()) + " " + name + " " + argTypes.length;
        for (Class<?> argType : argTypes)
            comparisonKey += " " + argType.getName();
        return comparisonKey;
    }

    @Override
    public int compareTo(FunctionData o) {
        return getComparisonKey().compareTo(o.getComparisonKey());
    }
}
