package io.github.toveri.openskill.models;

import io.github.toveri.openskill.Match;
import io.github.toveri.openskill.TeamRating;
import io.github.toveri.openskill.Rating;

import java.util.ArrayList;
import java.util.List;

import static io.github.toveri.openskill.Common.getAdjacentPairs;

/**
 * Bradley-Terry partial pairing model.
 */
public class BradleyTerryPart extends Model {
    /**
     * Bradley-Terry partial pairing model with default options.
     */
    public BradleyTerryPart() {
        super();
    }

    /**
     * Bradley-Terry partial pairing model with custom options.
     * @param options The custom model options.
     */
    public BradleyTerryPart(ModelOptions options) {
        super(options);
    }

    @Override
    protected Match compute(Match match, List<Double> ranks) {
        List<TeamRating> teamRatings = calculateTeamRatings(match, ranks);
        List<List<Rating>> teams = new ArrayList<>(match.teamCount());
        List<List<TeamRating>> teamsAdjacentPerTeam = getAdjacentPairs(teamRatings);
        for (int i = 0; i < teamRatings.size(); i++) {
            TeamRating teamI = teamRatings.get(i);
            List<TeamRating> teamsAdjacent = teamsAdjacentPerTeam.get(i);
            double omega = 0.0;
            double delta = 0.0;
            for (TeamRating teamQ : teamsAdjacent) {
                double cIq = Math.sqrt(teamI.sigmaSq + teamQ.sigmaSq + (2 * (beta * beta)));
                double pIq = 1 / (1 + Math.exp((teamQ.mu - teamI.mu) / cIq));
                double sigmaSquaredOverCIq = teamI.sigmaSq / cIq;
                double s = 0.0;
                if (teamQ.rank > teamI.rank) {
                    s = 1.0;
                } else if (teamQ.rank == teamI.rank) {
                    s = 0.5;
                }
                omega += sigmaSquaredOverCIq * (s - pIq);
                double gamma = gamma(cIq, teamRatings.size(), teamI.mu, teamI.sigmaSq, teamI.team, teamI.rank);
                delta += ((gamma * sigmaSquaredOverCIq) / cIq) * pIq * (1 - pIq);
            }
            teams.add(updateTeamRating(teamI, omega, delta));
        }
        return new Match(teams);
    }
}
