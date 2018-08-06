package frangel;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.BiPredicate;

public class Equals {
    public static boolean equalDouble(double d1, double d2) {
        if (Double.isNaN(d1) && Double.isNaN(d2))
            return true;
        return d1 == d2 || Math.abs(d1 - d2) < 1e-5;
    }

    public static boolean customEquals(Object expected, Object actual) {
        return customEquals(expected, actual, Collections.emptyMap());
    }

    public static boolean customEquals(Object expected, Object actual, Map<Class<?>, BiPredicate<Object, Object>> equalityTesters) {
        if (expected == null || actual == null) {
            return expected == actual;
        }
        Class<?> cls = expected.getClass();
        if (!cls.isAssignableFrom(actual.getClass())) {
            return false;
        }
        BiPredicate<Object, Object> tester = equalityTesters.get(cls);
        if (tester != null)
            return tester.test(expected, actual);
        for (Map.Entry<Class<?>, BiPredicate<Object, Object>> entry: equalityTesters.entrySet()) {
            Class<?> type = entry.getKey();
            if (type.isAssignableFrom(cls)) {
                return entry.getValue().test(expected, actual);
            }
        }
        if (cls.equals(Double.class)) {
            return equalDouble((Double) expected, (Double) actual);
        }
        if (cls.equals(Float.class)) {
            return equalDouble((Float) expected, (Float) actual);
        }
        if (cls.isPrimitive()) {
            if (cls.equals(double.class)) {
                return equalDouble((double) expected, (double) actual);
            }
            if (cls.equals(float.class)) {
                return equalDouble((float) expected, (float) actual);
            }
            return expected.equals(actual); /* .equals() suffices for primitive wrapper objects */
        }
        if (cls.isArray()) {
            int len1 = Array.getLength(expected);
            int len2 = Array.getLength(actual);
            if (len1 != len2)
                return false;
            for (int i = 0; i < len1; i++)
                if (!customEquals(Array.get(expected, i), Array.get(actual, i), equalityTesters))
                    return false;
            return true;
        }
        if (Iterable.class.isAssignableFrom(cls)) {
            /* If the actual object contains itself, using .equals() can produce a StackOverflowError */
            for (Object o : (Iterable<?>) actual)
                if (o == actual)
                    return false;
        }
        if (Queue.class.isAssignableFrom(cls)) {
            Queue<?> q1 = (Queue<?>) expected;
            Queue<?> q2 = (Queue<?>) actual;
            if (q1.size() != q2.size())
                return false;
            Iterator<?> it1 = q1.iterator();
            Iterator<?> it2 = q2.iterator();
            while (it1.hasNext()) {
                Object o1 = it1.next();
                Object o2 = it2.next();
                if (o1 == q1 || o2 == q2 || !customEquals(o1, o2, equalityTesters))
                    return false;
            }
            return true;
        }
        if (List.class.isAssignableFrom(cls)) {
            List<?> l1 = (List<?>) expected;
            List<?> l2 = (List<?>) actual;
            if (l1.size() != l2.size())
                return false;
            for (int i = 0; i < l1.size(); i++) {
                Object o1 = l1.get(i);
                Object o2 = l2.get(i);
                if (o1 == l1 || o2 == l2 || !customEquals(o1, o2, equalityTesters))
                    return false;
            }
            return true;
        }
        return actual.equals(expected);
    }
}
