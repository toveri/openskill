package org.toveri.openskill;

import org.hipparchus.distribution.continuous.NormalDistribution;

public class Statistics {
    private static final NormalDistribution nd = new NormalDistribution();

    public static double phiMajor(double x) {
        return nd.cumulativeProbability(x);
    }

    public static double phiMajorInverse(double x) {
        return nd.inverseCumulativeProbability(x);
    }

    public static double phiMinor(double x) {
        return nd.density(x);
    }
}
