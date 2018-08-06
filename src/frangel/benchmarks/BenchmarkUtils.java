package frangel.benchmarks;

import java.awt.Polygon;
import java.awt.geom.*;
import java.lang.reflect.Method;
import java.util.*;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.linear.RealMatrix;

public class BenchmarkUtils {
    public static Method getMethod(Class<?> cls, String name, Class<?>... params) {
        try {
            return cls.getDeclaredMethod(name, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @SafeVarargs
    public static <T> List<T> makeList(T... elements) {
        return new ArrayList<T>(Arrays.asList(elements));
    }

    @SafeVarargs
    public static <T> Queue<T> makeQueue(T... elements) {
        return new ArrayDeque<T>(Arrays.asList(elements));
    }

    @SafeVarargs
    public static <T> Set<T> makeSet(T... elements) {
        return new HashSet<T>(Arrays.asList(elements));
    }

    public static boolean equalsDouble(double d1, double d2) {
        if (Double.isNaN(d1) && Double.isNaN(d2))
            return true;
        return d1 == d2 || Math.abs(d1 - d2) < 1e-5;
    }

    public static boolean equalsPoint2D(Point2D p1, Point2D p2) {
        return equalsDouble(p1.getX(), p2.getX()) && equalsDouble(p1.getY(), p2.getY());
    }

    public static boolean equalsLine2D(Line2D line1, Line2D line2) {
        return equalsPoint2D(line1.getP1(), line2.getP1()) && equalsPoint2D(line1.getP2(), line2.getP2());
    }

    public static boolean equalsPath2D(Path2D p1, Path2D p2) {
        PathIterator it1 = p1.getPathIterator(null);
        PathIterator it2 = p2.getPathIterator(null);
        double[] coords1 = new double[6];
        double[] coords2 = new double[6];
        while (!it1.isDone() && !it2.isDone()) {
            if (it1.currentSegment(coords1) != it2.currentSegment(coords2))
                return false;
            if (!Arrays.equals(coords1, coords2)) // exact comparison
                return false;
            it1.next();
            it2.next();
        }
        return it1.isDone() && it2.isDone();
    }

    public static boolean equalsPolygon(Polygon p1, Polygon p2) {
        if (p1.npoints != p2.npoints)
            return false;
        for (int i = 0; i < p1.npoints; i++)
            if (p1.xpoints[i] != p2.xpoints[i] || p1.ypoints[i] != p2.ypoints[i])
                return false;
        return true;
    }

    public static boolean equalsRectangle2D(Rectangle2D r1, Rectangle2D r2) {
        return equalsDouble(r1.getX(), r2.getX()) && equalsDouble(r1.getY(), r2.getY()) &&
                equalsDouble(r1.getWidth(), r2.getWidth()) && equalsDouble(r1.getHeight(), r2.getHeight());
    }

    // Equality testers for SyPet

    public static boolean equalsRealMatrix(RealMatrix m1, RealMatrix m2) {
        if (m1.getRowDimension() != m2.getRowDimension() || m1.getColumnDimension() != m2.getColumnDimension())
            return false;
        for (int i = 0; i < m1.getRowDimension(); i++)
            for (int j = 0; j < m1.getColumnDimension(); j++)
                if (!equalsDouble(m1.getEntry(i, j), m2.getEntry(i, j)))
                    return false;
        return true;
    }

    public static boolean equalsVector2D(Vector2D v1, Vector2D v2) {
        return equalsDouble(v1.getX(), v2.getX()) && equalsDouble(v1.getY(), v2.getY());
    }
}
