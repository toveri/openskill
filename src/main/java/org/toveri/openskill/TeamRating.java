package org.toveri.openskill;

import java.util.List;

public class TeamRating {
    public double mu;
    public double sigmaSq;
    public List<Rating> team;
    public double rank;

    public TeamRating(double mu, double sigmaSq, List<Rating> team, double rank) {
        this.mu = mu;
        this.sigmaSq = sigmaSq;
        this.team = team;
        this.rank = rank;
    }
}
