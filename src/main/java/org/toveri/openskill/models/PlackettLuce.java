package org.toveri.openskill.models;

import org.toveri.openskill.Match;
import org.toveri.openskill.Rating;
import org.toveri.openskill.TeamRating;

import java.util.ArrayList;
import java.util.List;

public class PlackettLuce extends Model {
    public PlackettLuce() {
        super();
    }

    public PlackettLuce(ModelOptions options) {
        super(options);
    }

    // Will modify match.
    @Override
    public Match compute(Match match, List<Double> ranks) {
        List<TeamRating> teamRatings = calculateTeamRatings(match, ranks);
        double c = c(teamRatings);
        List<Double> sumQ = sumQ(teamRatings, c);
        List<Integer> a = a(teamRatings);
        List<List<Rating>> teams = new ArrayList<>(match.teamCount());
        for (int i = 0; i < teamRatings.size(); i++) {
            double omega = 0.0;
            double delta = 0.0;
            TeamRating teamI = teamRatings.get(i);
            double iMuOverC = Math.exp(teamI.mu / c);
            for (int q = 0; q < teamRatings.size(); q++) {
                double iMuOverCOverSumQ = iMuOverC / sumQ.get(q);
                TeamRating teamQ = teamRatings.get(q);
                if (teamQ.rank <= teamI.rank) {
                    delta += (iMuOverCOverSumQ * (1 - iMuOverCOverSumQ) / a.get(q));
                    if (q == i) {
                        omega += (1 - iMuOverCOverSumQ) / a.get(q);
                    } else {
                        omega -= iMuOverCOverSumQ / a.get(q);
                    }
                }
            }
            omega *= teamI.sigmaSq / c;
            delta *= teamI.sigmaSq / (c * c);
            delta *= gamma(c, teamRatings.size(), teamI.mu, teamI.sigmaSq, teamI.team, teamI.rank);
            teams.add(updateTeamRating(teamI, omega, delta));
        }
        return new Match(teams);
    }
}
