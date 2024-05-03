package io.github.toveri.openskill;

import org.hipparchus.distribution.continuous.NormalDistribution;

/**
 * Statistics used to calculate rating updates.
 * Calculations are done using a standard normal distribution.
 */
public final class Statistics {
    private static final NormalDistribution nd = new NormalDistribution();

    private Statistics() {}

    /**
     * The cumulative distribution function (CDF).
     * @param x The input value to calculate.
     * @return The calculated value.
     */
    public static double phiMajor(double x) {
        return nd.cumulativeProbability(x);
    }

    /**
     * The inverse cumulative distribution function.
     * @param x The input value to calculate.
     * @return The calculated value.
     */
    public static double phiMajorInverse(double x) {
        return nd.inverseCumulativeProbability(x);
    }

    /**
     * The probability density function (PDF).
     * @param x The value to calculate.
     * @return The calculated value.
     */
    public static double phiMinor(double x) {
        return nd.density(x);
    }
}
