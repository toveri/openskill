package io.github.toveri.openskill;

/**
 * The default values used for the models and ratings.
 */
public final class Constants {
    /**
     * The divisor for the mean to get the standard deviation, also used for ordinal.
     */
    public static final int Z = 3;
    /**
     * The default mean value.
     */
    public static final double MU = 25;
    /**
     * The default standard deviation.
     */
    public static final double SIGMA = MU / Z;
    /**
     * The default uncertainty value.
     */
    public static final double BETA = SIGMA / 2;
    /**
     * The default uncertainty value squared.
     */
    public static final double BETA_SQ = BETA * BETA;
    /**
     * The default minimum rating variance value.
     */
    public static final double TAU = MU / 300;
    /**
     * The default value to prevent negative posterior distributions.
     */
    public static final double KAPPA = 0.0001;

    private Constants() {}
}
