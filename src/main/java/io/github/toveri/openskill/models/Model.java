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

public abstract class Model {
    public final double mu;
    public final double sigma;
    public final double beta;
    public final double betaSq;
    public final double kappa;
    public final Gamma gammaFun;
    public final double tau;
    public final double tauSq;

    public Model() {
        this(new ModelOptionsBuilder().build());
    }

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

    protected static double c(List<TeamRating> teamRatings) {
        double totalTeamSigma = 0.0;
        for (TeamRating teamRating : teamRatings) {
            totalTeamSigma += teamRating.sigmaSq + Constants.BETA_SQ;
        }
        return Math.sqrt(totalTeamSigma);
    }

    protected static List<Double> sumQ(List<TeamRating> teamRatings, double c) {
        return teamRatings.stream()
                .map(qTeamRating -> teamRatings.stream()
                        .filter(iTeamRating -> iTeamRating.rank >= qTeamRating.rank)
                        .map(iTeamRating -> Math.exp(iTeamRating.mu / c))
                        .reduce(0.0, Double::sum)
                ).collect(Collectors.toList());
    }

    protected static List<Integer> a(List<TeamRating> teamRatings) {
        return teamRatings.stream()
                .map(iTeamRating -> ((int) teamRatings.stream().filter(
                        qTeamRating -> iTeamRating.rank == qTeamRating.rank).count()))
                .collect(Collectors.toList());
    }

    protected static List<TeamRating> calculateTeamRatings(Match match) {
        return calculateTeamRatings(match, null);
    }

    // Modifies teams in place.
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

    public Rating rating() {
        return new Rating(mu, sigma);
    }

    // Does not modify match.
    public Match rate(Match match) {
        List<Double> ranks = generateDefaultRanks(match.teamCount());
        RateOptions options = new RateOptions(ranks, true);
        return rate(match, options);
    }

    // Does not modify match.
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

    // Does modify match.
    protected abstract Match compute(Match match, List<Double> ranks);

    protected double gamma(double c, int k, double mu, double sigmaSq, List<Rating> team, double rank) {
        return gammaFun.gamma(c, k, mu, sigmaSq, team, rank);
    }

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
