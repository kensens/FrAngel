package frangel.interpreter;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import frangel.Settings;

public class Environment {
    private Map<String, Object> variables;

    public Environment() {
        variables = new HashMap<String, Object>();
    }

    public Object get(String name) throws Exception {
        if (!variables.containsKey(name))
            throw new Exception("Unknown variable name: " + name);
        return variables.get(name);
    }

    public void set(String name, Object var) {
        variables.put(name, var);
    }

    public void remove(String name) {
        variables.remove(name);
    }

    public boolean contains(String name) {
        return variables.containsKey(name);
    }

    public void incLoopCounter(String name) {
        variables.put(name, ((int) variables.get(name)) + 1);
    }
    public void resetLoopCounter(String name) {
        variables.put(name, 0);
    }

    @SuppressWarnings("rawtypes")
    private boolean checkObj(Object obj) {
        if (obj instanceof CharSequence && ((CharSequence) obj).length() > Settings.MAX_STRING_LEN)
            return true;
        if (obj.getClass().isArray() && Array.getLength(obj) > Settings.MAX_ARRAY_LEN)
            return true;
        if (obj instanceof java.awt.Polygon && ((java.awt.Polygon) obj).xpoints.length > Settings.MAX_ARRAY_LEN)
            return true;
        if (obj instanceof Collection) {
            Collection collection = (Collection) obj;
            if (collection.size() > Settings.MAX_COLLECTION_SIZE)
                return true;
            for (Object o : collection)
                if (o instanceof CharSequence && ((CharSequence) o).length() > Settings.MAX_STRING_LEN)
                    return true;
        }
        return false;
    }

    @SuppressWarnings("rawtypes")
    public boolean isTooLarge() {
        for (Object obj : variables.values()) {
            if (obj == null)
                continue;
            if (checkObj(obj))
                return true;
            if (obj instanceof Map && (checkObj(((Map) obj).keySet()) || checkObj(((Map) obj).values())))
                return true;
        }
        return false;
    }
}
