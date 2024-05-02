package io.github.toveri.openskill.models;

import io.github.toveri.openskill.Match;
import io.github.toveri.openskill.TeamRating;
import io.github.toveri.openskill.Rating;

import java.util.ArrayList;
import java.util.List;

import static io.github.toveri.openskill.Common.*;

public class ThurstoneMostellerPart extends Model {
    public ThurstoneMostellerPart() {
        super();
    }

    public ThurstoneMostellerPart(ModelOptions options) {
        super(options);
    }

    // Will modify match.
    @Override
    public Match compute(Match match, List<Double> ranks) {
        List<TeamRating> teamRatings = calculateTeamRatings(match, ranks);
        List<List<Rating>> teams = new ArrayList<>(match.teamCount());
        List<List<TeamRating>> teamsAdjacentPerTeam = ladderPairs(teamRatings);
        for (int i = 0; i < teamRatings.size(); i++) {
            TeamRating teamI = teamRatings.get(i);
            List<TeamRating> teamsAdjacent = teamsAdjacentPerTeam.get(i);
            double omega = 0.0;
            double delta = 0.0;
            for (TeamRating teamQ : teamsAdjacent) {
                double cIq = 2 * Math.sqrt(teamI.sigmaSq + teamQ.sigmaSq + (2 * (beta * beta)));
                double deltaMu = (teamI.mu - teamQ.mu) / cIq;
                double sigmaSqOverCIq = teamI.sigmaSq / cIq;
                double gamma = gamma(cIq, teamRatings.size(), teamI.mu, teamI.sigmaSq, teamI.team, teamI.rank);
                if (teamQ.rank > teamI.rank) {
                    omega += sigmaSqOverCIq * v(deltaMu, kappa / cIq);
                    delta += gamma * sigmaSqOverCIq / cIq * w(deltaMu, kappa / cIq);
                } else if (teamQ.rank < teamI.rank) {
                    omega += -sigmaSqOverCIq * v(-deltaMu, kappa / cIq);
                    delta += gamma * sigmaSqOverCIq / cIq * w(-deltaMu, kappa / cIq);
                } else {
                    omega += sigmaSqOverCIq * vt(deltaMu, kappa / cIq);
                    delta += gamma * sigmaSqOverCIq / cIq * wt(deltaMu, kappa / cIq);
                }
            }
            teams.add(updateTeamRating(teamI, omega, delta));
        }
        return new Match(teams);
    }
}
