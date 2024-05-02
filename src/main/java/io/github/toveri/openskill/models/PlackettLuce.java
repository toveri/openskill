package io.github.toveri.openskill.models;

import io.github.toveri.openskill.Match;
import io.github.toveri.openskill.TeamRating;
import io.github.toveri.openskill.Rating;

import java.util.ArrayList;
import java.util.List;

/**
 * Plackett-Luce model.
 */
public class PlackettLuce extends Model {
    /**
     * Plackett-Luce full pairing model with default options.
     */
    public PlackettLuce() {
        super();
    }

    /**
     * Plackett-Luce model with custom options.
     * @param options The custom model options.
     */
    public PlackettLuce(ModelOptions options) {
        super(options);
    }

    @Override
    protected Match compute(Match match, List<Double> ranks) {
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
