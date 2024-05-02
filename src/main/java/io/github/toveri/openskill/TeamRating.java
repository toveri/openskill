package io.github.toveri.openskill;

import java.util.List;

/**
 * Represents the rating of an entire team.
 */
public class TeamRating {
    /**
     * The mean value.
     */
    public double mu;
    /**
     * The standard deviation squared.
     */
    public double sigmaSq;
    /**
     * The list of ratings in the team.
     */
    public List<Rating> team;
    /**
     * The rank of the team.
     */
    public double rank;

    /**
     * Create a team rating with the given parameters.
     * @param mu The mean value.
     * @param sigmaSq The standard deviation squared.
     * @param team The list of ratings in the team.
     * @param rank he rank of the team.
     */
    public TeamRating(double mu, double sigmaSq, List<Rating> team, double rank) {
        this.mu = mu;
        this.sigmaSq = sigmaSq;
        this.team = team;
        this.rank = rank;
    }
}
