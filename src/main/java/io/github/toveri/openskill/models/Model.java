package io.github.toveri.openskill.models;

import io.github.toveri.openskill.*;
import org.hipparchus.stat.ranking.NaNStrategy;
import org.hipparchus.stat.ranking.NaturalRanking;
import org.hipparchus.stat.ranking.TiesStrategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static io.github.toveri.openskill.Common.unwind;
import static io.github.toveri.openskill.Statistics.phiMajor;
import static io.github.toveri.openskill.Statistics.phiMajorInverse;

/**
 * Represents a rating model.
 */
public abstract class Model {
    protected final double mu;
    protected final double sigma;
    protected final double beta;
    protected final double betaSq;
    protected final double kappa;
    protected final Gamma gammaFun;
    protected final double tau;
    protected final double tauSq;

    /**
     * Model with default options.
     */
    public Model() {
        this(new ModelOptionsBuilder().build());
    }

    /**
     * Model with custom options.
     * @param options The custom model options.
     */
    public Model(ModelOptions options) {
        mu = options.mu();
        sigma = options.sigma();
        beta = options.beta();
        betaSq = beta * beta;
        kappa = options.kappa();
        gammaFun = options.gammaFun();
        tau = options.tau();
        tauSq = tau * tau;
    }

    /**
     * Calculate the square root of the sum of each teams' sigma values squared and beta squared added.
     * @param teamRatings The list of team ratings.
     * @return The calculated c value.
     */
    protected static double c(List<TeamRating> teamRatings) {
        double sum = 0.0;
        for (TeamRating teamRating : teamRatings) {
            sum += teamRating.sigmaSq + Constants.BETA_SQ;
        }
        return Math.sqrt(sum);
    }

    /**
     * Calculate the values of each team's mu / c, raised to e.
     * @param teamRatings The list of team ratings.
     * @param c The value for c.
     * @return The list of values calculated
     */
    protected static List<Double> sumQ(List<TeamRating> teamRatings, double c) {
        return teamRatings.stream()
                .map(qTeamRating -> teamRatings.stream()
                        .filter(iTeamRating -> iTeamRating.rank >= qTeamRating.rank)
                        .map(iTeamRating -> Math.exp(iTeamRating.mu / c))
                        .reduce(0.0, Double::sum)
                ).collect(Collectors.toList());
    }

    /**
     * Count the number of times a team's rank appears in the list of team ratings.
     * @param teamRatings The list of team ratings.
     * @return The list of counts for each rank.
     */
    protected static List<Integer> a(List<TeamRating> teamRatings) {
        return teamRatings.stream()
                .map(iTeamRating -> ((int) teamRatings.stream().filter(
                        qTeamRating -> iTeamRating.rank == qTeamRating.rank).count()))
                .collect(Collectors.toList());
    }

    /**
     * Calculate team ratings using default ranks.
     * @param match The match to be used.
     * @return The list of team ratings.
     */
    protected static List<TeamRating> calculateTeamRatings(Match match) {
        return calculateTeamRatings(match, null);
    }

    /**
     * Calculate team ratings using given ranks.
     * @param match The match to be used.
     * @param ranks The list of ranks to be used.
     * @return The list of team ratings.
     */
    protected static List<TeamRating> calculateTeamRatings(Match match, List<Double> ranks) {
        List<Double> placements;
        if (ranks != null) {
            placements = calculatePlacements(ranks);
        } else {
            placements = calculatePlacements(generateDefaultRanks(match.teamCount()));
        }
        List<TeamRating> teamRatings = new ArrayList<>(match.teamCount());
        for (int i = 0; i < placements.size(); i++) {
            teamRatings.add(calculateTeamRating(match.getTeam(i), placements.get(i)));
        }
        return teamRatings;
    }

    private static TeamRating calculateTeamRating(List<Rating> team) {
        return calculateTeamRating(team, 0.0);
    }

    private static TeamRating calculateTeamRating(List<Rating> team, Double rank) {
        double muSum = 0.0;
        double sigmaSqSum = 0.0;
        for (Rating rating : team) {
            muSum += rating.mu;
            sigmaSqSum += (rating.sigma * rating.sigma);
        }
        return new TeamRating(muSum, sigmaSqSum, team, rank);
    }

    private static List<Double> calculatePlacements(List<Double> ranks) {
        NaturalRanking naturalRanking = new NaturalRanking(NaNStrategy.FAILED, TiesStrategy.MINIMUM);
        double[] ranksArray = ranks.stream().mapToDouble(Double::doubleValue).toArray();
        return Arrays.stream(naturalRanking.rank(ranksArray)).boxed().toList();
    }

    private static List<Double> generateDefaultRanks(int count) {
        return IntStream.range(1, count + 1).mapToDouble(v -> v).boxed().toList();
    }

    /**
     * Creates a rating object with the defaults of this model.
     * @return The rating object with default values.
     */
    public Rating rating() {
        return new Rating(mu, sigma);
    }

    /**
     * Rates the match based on the default rank order.
     * @param match The match to rate.
     * @return The Match with the rating applied.
     */
    public Match rate(Match match) {
        List<Double> ranks = generateDefaultRanks(match.teamCount());
        RateOptions options = new RateOptions(ranks, true);
        return rate(match, options);
    }

    /**
     * Rates the match based on the given options.
     * @param match The match to rate.
     * @param options The options to use (ranks or scores).
     * @return The Match with the rating applied.
     */
    public Match rate(Match match, RateOptions options) {
        match = new Match(match);
        for (List<Rating> team : match.getTeams()) {
            for (Rating rating : team) {
                rating.sigma = Math.sqrt((rating.sigma * rating.sigma) + tauSq);
            }
        }
        List<Double> placements = new ArrayList<>(options.ranks());
        // If necessary convert scores (larger better) to ranks (smaller better).
        if (!options.lowerIsBetter()) {
            placements = placements.stream()
                    .map(placement -> -placement)
                    .collect(Collectors.toCollection(ArrayList::new));
        }
        Common.Pair<List<List<Rating>>, List<Double>> teamsUnwound = unwind(match.getTeams(), placements);
        Match orderedMatch = new Match(teamsUnwound.a());
        List<Double> tenet = teamsUnwound.b();
        Collections.sort(placements);
        return new Match(unwind(compute(orderedMatch, placements).getTeams(), tenet).a());
    }

    /**
     * Predict the win probability for each team in the match.
     * @param match The match to predict wins for.
     * @return The list of probabilities for each team's win.
     */
    public List<Double> predictWin(Match match) {
        int teamCount = match.teamCount();
        if (teamCount == 2) {
            int playerCount = match.getTeams().get(0).size() + match.getTeams().get(1).size();
            List<TeamRating> teamRatings = calculateTeamRatings(match);
            double t1WinProbability = phiMajor(
                    (teamRatings.get(0).mu - teamRatings.get(1).mu)
                            / Math.sqrt(playerCount * betaSq
                            + teamRatings.get(0).sigmaSq
                            + teamRatings.get(1).sigmaSq)
            );
            return List.of(t1WinProbability, 1 - t1WinProbability);
        }
        List<Double> pairProbabilities = new ArrayList<>(teamCount * (teamCount - 1));
        for (int i = 0; i < match.teamCount(); i++) {
            for (int j = 0; j < match.teamCount(); j++) {
                if (i == j) {
                    continue;
                }
                List<Rating> t1 = match.getTeam(i);
                List<Rating> t2 = match.getTeam(j);
                TeamRating t1Rating = calculateTeamRating(t1);
                TeamRating t2Rating = calculateTeamRating(t2);
                pairProbabilities.add(phiMajor((t1Rating.mu - t2Rating.mu)
                        / Math.sqrt(teamCount * betaSq + t1Rating.sigmaSq + t2Rating.sigmaSq)
                ));
            }
        }
        double denom = (teamCount * (teamCount - 1)) / 2.0;
        List<Double> winProbabilities = new ArrayList<>(match.teamCount());
        for (int i = 0; i < teamCount; i++) {
            double probabilitySum = 0.0;
            for (int j = i * (teamCount - 1); j < (i + 1) * (teamCount - 1); j++) {
                probabilitySum += pairProbabilities.get(j);
            }
            winProbabilities.add(probabilitySum / denom);
        }
        return winProbabilities;
    }

    /**
     * Predict the draw probability for the match.
     * @param match The match to predict the draw for.
     * @return The probability for a draw.
     */
    public double predictDraw(Match match) {
        int teamCount = match.teamCount();
        int playerCount = match.getTeams().stream().map(List::size).reduce(0, Integer::sum);
        double drawMargin = Math.sqrt(playerCount) * beta * phiMajorInverse((1 + (1 / (double) playerCount)) / 2.0);
        List<Double> pairProbabilities = new ArrayList<>(teamCount * (teamCount - 1));
        for (int i = 0; i < match.teamCount(); i++) {
            for (int j = 0; j < match.teamCount(); j++) {
                if (i == j) {
                    continue;
                }
                List<Rating> t1 = match.getTeam(i);
                List<Rating> t2 = match.getTeam(j);
                TeamRating t1Rating = calculateTeamRating(t1);
                TeamRating t2Rating = calculateTeamRating(t2);
                pairProbabilities.add(phiMajor((drawMargin - t1Rating.mu + t2Rating.mu)
                                / Math.sqrt(teamCount * betaSq + t1Rating.sigmaSq + t2Rating.sigmaSq))
                                - phiMajor((t1Rating.mu - t2Rating.mu - drawMargin)
                                / Math.sqrt(teamCount * betaSq + t1Rating.sigmaSq + t2Rating.sigmaSq)
                        )
                );
            }
        }
        double denom = teamCount > 2 ? teamCount * (teamCount - 1) : 1.0;
        return Math.abs(pairProbabilities.stream().reduce(0.0, Double::sum)) / denom;
    }

    /**
     * Predict the most probable rank for each team in the match.
     * This will not add upp to a probability of 1.0 by itself, since a draw is possible as well.
     * @param match The match to predict ranks for.
     * @return The list of each team's most probable rank and that probability.
     */
    public List<List<Double>> predictRank(Match match) {
        int teamCount = match.teamCount();
        int playerCount = match.getTeams().stream().map(List::size).reduce(0, Integer::sum);
        double drawMargin = Math.sqrt(playerCount) * beta * phiMajorInverse((1 + (1 / (double) playerCount)) / 2.0);
        List<Double> pairProbabilities = new ArrayList<>(teamCount * (teamCount - 1));
        for (int i = 0; i < match.teamCount(); i++) {
            for (int j = 0; j < match.teamCount(); j++) {
                if (i == j) {
                    continue;
                }
                List<Rating> t1 = match.getTeam(i);
                List<Rating> t2 = match.getTeam(j);
                TeamRating t1Rating = calculateTeamRating(t1);
                TeamRating t2Rating = calculateTeamRating(t2);
                pairProbabilities.add(phiMajor((t1Rating.mu - t2Rating.mu - drawMargin)
                        / Math.sqrt(teamCount * betaSq + t1Rating.sigmaSq + t2Rating.sigmaSq)
                ));
            }
        }
        double denom = (teamCount * (teamCount - 1)) / 2.0;
        List<Double> winProbabilities = new ArrayList<>(match.teamCount());
        for (int i = 0; i < teamCount; i++) {
            double probabilitySum = 0.0;
            for (int j = i * (teamCount - 1); j < (i + 1) * (teamCount - 1); j++) {
                probabilitySum += pairProbabilities.get(j);
            }
            winProbabilities.add(probabilitySum / denom);
        }
        List<Double> rankProbabilities = winProbabilities.stream().map(Math::abs).toList();
        List<Double> placements = calculatePlacements(rankProbabilities);
        List<List<Double>> rankPredictions = new ArrayList<>(placements.size());
        for (int i = 0; i < placements.size(); i++) {
            rankPredictions.add(List.of(placements.get(i), rankProbabilities.get(i)));
        }
        return rankPredictions;
    }

    /**
     * Apply the model's rating algorithm to the match based on the ranks supplied.
     * @param match The match to compute.
     * @param ranks The ranks to use.
     * @return The match with the model's rating algorithm applied.
     */
    protected abstract Match compute(Match match, List<Double> ranks);

    /**
     * The function that controls how fast variance is reduced.
     * @param c The value for c.
     * @param k The count of teams.
     * @param mu The mean value.
     * @param sigmaSq The value for standard deviation squared.
     * @param team The team (list of ratings).
     * @param rank The rank of the team.
     * @return
     */
    protected double gamma(double c, int k, double mu, double sigmaSq, List<Rating> team, double rank) {
        return gammaFun.gamma(c, k, mu, sigmaSq, team, rank);
    }

    /**
     * Update the team rating based on the supplied omega and delta values.
     * @param teamRating The team rating to update.
     * @param omega The factor apply to the rating mu value.
     * @param delta The factor to apply to the rating sigma value.
     * @return The list of updated ratings.
     */
    protected List<Rating> updateTeamRating(TeamRating teamRating, double omega, double delta) {
        List<Rating> teamRatingsUpdated = new ArrayList<>(teamRating.team.size());
        for (Rating rating : teamRating.team) {
            double mu = rating.mu;
            double sigma = rating.sigma;
            mu += (sigma * sigma / teamRating.sigmaSq) * omega;
            sigma *= Math.sqrt(Math.max(1 - ((sigma * sigma) / teamRating.sigmaSq) * delta, kappa));
            Rating ratingUpdated = new Rating(rating);
            ratingUpdated.mu = mu;
            ratingUpdated.sigma = sigma;
            teamRatingsUpdated.add(ratingUpdated);
        }
        return teamRatingsUpdated;
    }
}
