package org.toveri.openskill;

import java.util.List;

public record RateOptions(List<Double> ranks, boolean lowerIsBetter) {
    public RateOptions(List<Double> ranks) {
        this(ranks, true);
    }
}
