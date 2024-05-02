package io.github.toveri.openskill;

import java.util.List;

/**
 * Options for the rate method of a model.
 * @param ranks The order of the teams represented by numbers.
 * @param lowerIsBetter If lower ranks is better or not.
 */
public record RateOptions(List<Double> ranks, boolean lowerIsBetter) {
    /**
     * Options for the rate method of a model.
     * Assuming that lower values for rank is better.
     * @param ranks The order of the teams represented by numbers.
     */
    public RateOptions(List<Double> ranks) {
        this(ranks, true);
    }
}
