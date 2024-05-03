package io.github.toveri.openskill;

import java.util.List;

/**
 * The functional interface for controlling how fast variance is reduced.
 */
@FunctionalInterface
public interface Gamma {
    /**
     * The function that controls how fast variance is reduced.
     * @param c The value for c.
     * @param k The count of teams.
     * @param mu The mean value.
     * @param sigmaSq The value for standard deviation squared.
     * @param team The team (list of ratings).
     * @param rank The rank of the team.
     * @return The gamma value.
     */
    double gamma(double c, int k, double mu, double sigmaSq, List<Rating> team, double rank);
}
