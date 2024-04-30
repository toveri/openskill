package org.toveri.openskill.models;

import org.toveri.openskill.Match;
import org.toveri.openskill.Rating;
import org.toveri.openskill.TeamRating;

import java.util.ArrayList;
import java.util.List;

public class BradleyTerryFull extends Model {
    public BradleyTerryFull() {
        super();
    }

    public BradleyTerryFull(ModelOptions options) {
        super(options);
    }

    // Will modify match.
    @Override
    public Match compute(Match match, List<Double> ranks) {
        List<TeamRating> teamRatings = calculateTeamRatings(match, ranks);
        List<List<Rating>> teams = new ArrayList<>(match.teamCount());
        for (int i = 0; i < teamRatings.size(); i++) {
            double omega = 0.0;
            double delta = 0.0;
            TeamRating teamI = teamRatings.get(i);
            for (int q = 0; q < teamRatings.size(); q++) {
                if (q == i) {
                    continue;
                }
                TeamRating teamQ = teamRatings.get(q);
                double cIq = Math.sqrt(teamI.sigmaSq + teamQ.sigmaSq + (2 * (beta * beta)));
                double pIq = 1 / (1 + Math.exp((teamQ.mu - teamI.mu) / cIq));
                double sigmaSqOverCIq = teamI.sigmaSq / cIq;
                double s = 0.0;
                if (teamQ.rank > teamI.rank) {
                    s = 1.0;
                } else if (teamQ.rank == teamI.rank) {
                    s = 0.5;
                }
                double gamma = gamma(cIq, teamRatings.size(), teamI.mu, teamI.sigmaSq, teamI.team, teamI.rank);
                omega += sigmaSqOverCIq * (s - pIq);
                delta += ((gamma * sigmaSqOverCIq) / cIq) * pIq * (1 - pIq);
            }
            teams.add(updateTeamRating(teamI, omega, delta));
        }
        return new Match(teams);
    }
}
