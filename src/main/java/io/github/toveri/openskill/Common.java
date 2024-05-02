package io.github.toveri.openskill;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Common {
    public static <T> List<List<T>> ladderPairs(List<T> ratings) {
        if (ratings.isEmpty()) {
            return List.of(List.of());
        }
        List<T> left = new ArrayList<>(ratings.size());
        left.add(null);
        left.addAll(ratings.subList(0, ratings.size() - 1));
        List<T> right = new ArrayList<>(ratings.size());
        right.addAll(ratings.subList(1, ratings.size()));
        right.add(null);
        List<List<T>> ladderPairs = new ArrayList<>(ratings.size());
        for (int i = 0; i < ratings.size(); i++) {
            T l = left.get(i);
            T r = right.get(i);
            if (l != null && r != null) {
                ladderPairs.add(List.of(l, r));
            } else if (l != null) {
                ladderPairs.add(List.of(l));
            } else if (r != null) {
                ladderPairs.add(List.of(r));
            } else {
                ladderPairs.add(List.of());
            }
        }
        return ladderPairs;
    }

    public static <T> Pair<List<T>, List<Double>> unwind(List<T> values, List<Double> tenets) {
        List<Triplet<Double, Double, T>> zipped = new ArrayList<>(tenets.size());
        for (int i = 0; i < tenets.size(); i++) {
            zipped.add(new Triplet<>(tenets.get(i), (double) i, values.get(i)));
        }
        zipped.sort(Comparator.comparingDouble(triplet -> triplet.a));
        List<T> valuesSorted = new ArrayList<>(zipped.size());
        List<Double> indicesSorted = new ArrayList<>(zipped.size());
        for (Triplet<Double, Double, T> triplet : zipped) {
            valuesSorted.add(triplet.c);
            indicesSorted.add(triplet.b);
        }
        return new Pair<>(valuesSorted, indicesSorted);
    }

    public static double v(double x, double t) {
        double xt = x - t;
        double denom = Statistics.phiMajor(xt);
        return denom > 0 ? Statistics.phiMinor(xt) / denom : -xt;
    }

    public static double w(double x, double t) {
        double xt = x - t;
        double denom = Statistics.phiMajor(xt);
        if (denom > 0) {
            return v(x, t) * (v(x, t) + xt);
        }
        return x < 0 ? 1 : 0;
    }

    public static double vt(double x, double t) {
        double xx = Math.abs(x);
        double denom = Statistics.phiMajor(t - xx) - Statistics.phiMajor(-t - xx);
        if (denom > 0) {
            double a = Statistics.phiMinor(-t - xx) - Statistics.phiMinor(t - xx);
            return (x < 0 ? -a : a) / denom;
        }
        return x < 0 ? -x - t : -x + t;
    }

    public static double wt(double x, double t) {
        double xx = Math.abs(x);
        double denom = Statistics.phiMajor(t - xx) - Statistics.phiMajor(-t - xx);
        return denom > 0 ? ((t - xx) * Statistics.phiMinor(t - xx) + (t + xx) * Statistics.phiMinor(-t - xx)) / denom
                + vt(x, t) * vt(x, t)
                : 1.0;
    }

    public record Pair<A, B>(A a, B b) {
    }

    public record Triplet<A, B, C>(A a, B b, C c) {
    }
}

