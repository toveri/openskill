package io.github.toveri.openskill;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Common utilities.
 */
public class Common {
    private Common () {}

    /**
     * Calculate the list of pairs of objects that are adjacent in the list.
     * @param objects The list of objects.
     * @return The list of adjacent pairs.
     * @param <T> The type for object.
     */
    public static <T> List<List<T>> getAdjacentPairs(List<T> objects) {
        if (objects.isEmpty()) {
            return List.of(List.of());
        }
        List<T> left = new ArrayList<>(objects.size());
        left.add(null);
        left.addAll(objects.subList(0, objects.size() - 1));
        List<T> right = new ArrayList<>(objects.size());
        right.addAll(objects.subList(1, objects.size()));
        right.add(null);
        List<List<T>> adjacentPairs = new ArrayList<>(objects.size());
        for (int i = 0; i < objects.size(); i++) {
            T l = left.get(i);
            T r = right.get(i);
            if (l != null && r != null) {
                adjacentPairs.add(List.of(l, r));
            } else if (l != null) {
                adjacentPairs.add(List.of(l));
            } else if (r != null) {
                adjacentPairs.add(List.of(r));
            } else {
                adjacentPairs.add(List.of());
            }
        }
        return adjacentPairs;
    }

    /**
     * Sorts a list of objects based on a given order, and returns the sorted list
     * along with the list of tenets (used to sort the objects in their original order).
     * @param objects The list of objects to sort.
     * @param tenets The order to sort in.
     * @return The sorted list of objects and the tenets.
     * @param <T> The type for object.
     */
    public static <T> Pair<List<T>, List<Double>> unwind(List<T> objects, List<Double> tenets) {
        List<Triplet<Double, Double, T>> zipped = new ArrayList<>(tenets.size());
        for (int i = 0; i < tenets.size(); i++) {
            zipped.add(new Triplet<>(tenets.get(i), (double) i, objects.get(i)));
        }
        zipped.sort(Comparator.comparingDouble(triplet -> triplet.a));
        List<T> objectsSorted = new ArrayList<>(zipped.size());
        List<Double> indicesSorted = new ArrayList<>(zipped.size());
        for (Triplet<Double, Double, T> triplet : zipped) {
            objectsSorted.add(triplet.c);
            indicesSorted.add(triplet.b);
        }
        return new Pair<>(objectsSorted, indicesSorted);
    }

    /**
     * The function V defined in the Weng-Lin paper.
     * @param x A number.
     * @param t A number.
     * @return A number.
     */
    public static double v(double x, double t) {
        double xt = x - t;
        double denom = Statistics.phiMajor(xt);
        return denom > 0 ? Statistics.phiMinor(xt) / denom : -xt;
    }

    /**
     * The function W defined in the Weng-Lin paper.
     * @param x A number.
     * @param t A number.
     * @return A number.
     */
    public static double w(double x, double t) {
        double xt = x - t;
        double denom = Statistics.phiMajor(xt);
        if (denom > 0) {
            return v(x, t) * (v(x, t) + xt);
        }
        return x < 0 ? 1 : 0;
    }

    /**
     * The function Ṽ defined in the Weng-Lin paper.
     * @param x A number.
     * @param t A number.
     * @return A number.
     */
    public static double vt(double x, double t) {
        double xx = Math.abs(x);
        double denom = Statistics.phiMajor(t - xx) - Statistics.phiMajor(-t - xx);
        if (denom > 0) {
            double a = Statistics.phiMinor(-t - xx) - Statistics.phiMinor(t - xx);
            return (x < 0 ? -a : a) / denom;
        }
        return x < 0 ? -x - t : -x + t;
    }

    /**
     * The function W̃ defined in the Weng-Lin paper.
     * @param x A number.
     * @param t A number.
     * @return A number.
     */
    public static double wt(double x, double t) {
        double xx = Math.abs(x);
        double denom = Statistics.phiMajor(t - xx) - Statistics.phiMajor(-t - xx);
        return denom > 0 ? ((t - xx) * Statistics.phiMinor(t - xx) + (t + xx) * Statistics.phiMinor(-t - xx)) / denom
                + vt(x, t) * vt(x, t)
                : 1.0;
    }

    /**
     * A container for a pair of objects.
     * @param a Value a.
     * @param b Value b.
     * @param <A> Type for a.
     * @param <B> Type for b.
     */
    public record Pair<A, B>(A a, B b) {
    }

    private record Triplet<A, B, C>(A a, B b, C c) {
    }
}

