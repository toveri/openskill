package org.toveri.openskill;

import java.util.List;

@FunctionalInterface
public interface Gamma {
    double gamma(double c, int k, double mu, double sigmaSq, List<Rating> team, double rank);
}
