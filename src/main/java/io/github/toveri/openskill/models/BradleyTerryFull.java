package io.github.toveri.openskill.models;

import io.github.toveri.openskill.Match;
import io.github.toveri.openskill.TeamRating;
import io.github.toveri.openskill.Rating;

import java.util.ArrayList;
import java.util.List;

/**
 * Bradley-Terry full pairing model.
 */
public class BradleyTerryFull extends Model {
    /**
     * Bradley-Terry full pairing model with default options.
     */
    public BradleyTerryFull() {
        super();
    }

    /**
     * Bradley-Terry full pairing model with custom options.
     * @param options The custom model options.
     */
    public BradleyTerryFull(ModelOptions options) {
        super(options);
    }

    @Override
    protected Match compute(Match match, List<Double> ranks) {
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
