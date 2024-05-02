package io.github.toveri.openskill.models;

import io.github.toveri.openskill.Match;
import io.github.toveri.openskill.TeamRating;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import io.github.toveri.openskill.Rating;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ModelTest {
    static final double DELTA = 1e-15;

    @Test
    void testCalculateTeamRatingsAggregates() {
        List<Rating> t1 = List.of(new Rating());
        List<Rating> t2 = List.of(new Rating(), new Rating());
        Match match = new Match(List.of(t1, t2));
        List<TeamRating> result = Model.calculateTeamRatings(match);
        assertAll(
                () -> assertEquals(25, result.get(0).mu),
                () -> assertEquals(50, result.get(1).mu),
                () -> assertEquals(69.44444444444446, result.get(0).sigmaSq),
                () -> assertEquals(138.8888888888889, result.get(1).sigmaSq)
        );
    }

    @Test
    void testCalculateTeamRatings5v5() {
        List<Rating> t1 = List.of(new Rating(), new Rating(), new Rating(), new Rating(), new Rating());
        List<Rating> t2 = List.of(new Rating(), new Rating(), new Rating(), new Rating(), new Rating());
        Match match = new Match(List.of(t1, t2));
        List<TeamRating> result = Model.calculateTeamRatings(match);
        assertAll(
                () -> assertEquals(125, result.get(0).mu),
                () -> assertEquals(125, result.get(1).mu),
                () -> assertEquals(347.2222222222223, result.get(0).sigmaSq),
                () -> assertEquals(347.2222222222223, result.get(1).sigmaSq)
        );
    }

    @Test
    void testCalculateTeamRatings5v5v5WithRanks() {
        List<Rating> t1 = List.of(new Rating(), new Rating(), new Rating(), new Rating(), new Rating());
        List<Rating> t2 = List.of(new Rating(), new Rating(), new Rating(), new Rating(), new Rating());
        List<Rating> t3 = List.of(new Rating(), new Rating(), new Rating(), new Rating(), new Rating());
        Match match = new Match(List.of(t1, t2, t3));
        List<Double> ranks = List.of(3.0, 1.0, 2.0);
        List<TeamRating> result = Model.calculateTeamRatings(match, ranks);
        assertAll(
                () -> assertEquals(125, result.get(0).mu),
                () -> assertEquals(125, result.get(1).mu),
                () -> assertEquals(125, result.get(2).mu),
                () -> assertEquals(347.2222222222223, result.get(0).sigmaSq),
                () -> assertEquals(347.2222222222223, result.get(1).sigmaSq),
                () -> assertEquals(347.2222222222223, result.get(2).sigmaSq)
        );
    }

    @Test
    void testGamma() {
        List<Rating> t1 = List.of(new Rating());
        Model model = getInstance();
        assertAll(
                () -> assertEquals(1, model.gamma(2, 5, 3, 4, t1, 0)),
                () -> assertEquals(2, model.gamma(2, 5, 3, 16, t1, 0)),
                () -> assertEquals(4, model.gamma(2, 5, 3, 64, t1, 0))
        );
    }

    @Test
    void testC() {
        Rating r = new Rating();
        double sigmaSq = r.sigma * r.sigma;
        List<TeamRating> teamRatings = List.of(
                new TeamRating(0, 1 * sigmaSq, null, 0),
                new TeamRating(0, 2 * sigmaSq, null, 0)
        );
        Assertions.assertEquals(15.590239111558091, Model.c(teamRatings));
        teamRatings = List.of(
                new TeamRating(0, 5 * sigmaSq, null, 0),
                new TeamRating(0, 5 * sigmaSq, null, 0)
        );
        Assertions.assertEquals(27.003086243366084, Model.c(teamRatings));
    }

    @Test
    void testA() {
        List<TeamRating> teamRatings = List.of(
                new TeamRating(0, 0, null, 1),
                new TeamRating(0, 0, null, 2)
        );
        Assertions.assertEquals(List.of(1, 1), Model.a(teamRatings));
        teamRatings = List.of(
                new TeamRating(0, 0, null, 1),
                new TeamRating(0, 0, null, 2),
                new TeamRating(0, 0, null, 3),
                new TeamRating(0, 0, null, 4)
        );
        Assertions.assertEquals(List.of(1, 1, 1, 1), Model.a(teamRatings));
        teamRatings = List.of(
                new TeamRating(0, 0, null, 1),
                new TeamRating(0, 0, null, 1),
                new TeamRating(0, 0, null, 1),
                new TeamRating(0, 0, null, 4)
        );
        Assertions.assertEquals(List.of(3, 3, 3, 1), Model.a(teamRatings));
    }

    @Test
    void testSumQ() {
        Rating r = new Rating();
        double sigmaSq = (r.sigma * r.sigma);
        List<TeamRating> teamRatings = List.of(
                new TeamRating(1 * r.mu, 1 * sigmaSq, null, 1),
                new TeamRating(2 * r.mu, 2 * sigmaSq, null, 2)
        );
        double c = Model.c(teamRatings);
        Assertions.assertEquals(List.of(29.67892702634643, 24.70819334370875), Model.sumQ(teamRatings, c));
        teamRatings = List.of(
                new TeamRating(5 * r.mu, 5 * sigmaSq, null, 1),
                new TeamRating(5 * r.mu, 5 * sigmaSq, null, 2)
        );
        c = Model.c(teamRatings);
        Assertions.assertEquals(List.of(204.84378810598616, 102.42189405299308), Model.sumQ(teamRatings, c));
    }

    @Test
    void testPredictWinProbabilitiesSumTo1() {
        Rating r1 = new Rating();
        Rating r2 = new Rating(32.444, 5.123);
        Rating r3 = new Rating(73.381, 1.421);
        Rating r4 = new Rating(25.188, 6.2111);
        List<Rating> t1 = List.of(r1, r2);
        List<Rating> t2 = List.of(r3, r4);
        Model model = getInstance();
        Match match = new Match(List.of(t1, t2));
        List<Double> winProbabilities = model.predictWin(match);
        assertEquals(1.0, winProbabilities.stream().reduce(0.0, Double::sum));
        match = new Match(List.of(t1, t2, List.of(r2), List.of(r1), List.of(r3)));
        winProbabilities = model.predictWin(match);
        assertEquals(1.0, winProbabilities.stream().reduce(0.0, Double::sum), DELTA);
    }

    @Test
    void testPredictWinProbabilitiesAreAccurate() {
        Rating r1 = new Rating();
        Rating r2 = new Rating(32.444, 5.123);
        Rating r3 = new Rating(73.381, 1.421);
        Rating r4 = new Rating(25.188, 6.2111);
        List<Rating> t1 = List.of(r1, r2);
        List<Rating> t2 = List.of(r3, r4);
        Model model = getInstance();
        Match match = new Match(List.of(t1, t2));
        List<Double> winProbabilities2v2 = model.predictWin(match);
        assertAll(
                () -> assertEquals(0.002070691134693502, winProbabilities2v2.get(0)),
                () -> assertEquals(0.9979293088653065, winProbabilities2v2.get(1))
        );
        match = new Match(List.of(t1, t2, List.of(r2), List.of(r1), List.of(r3)));
        List<Double> winProbabilitiesFFA = model.predictWin(match);
        assertAll(
                () -> assertEquals(0.20610382204399275, winProbabilitiesFFA.get(0)),
                () -> assertEquals(0.39836383442593964, winProbabilitiesFFA.get(1)),
                () -> assertEquals(0.07510464625428584, winProbabilitiesFFA.get(2)),
                () -> assertEquals(0.031133989129221024, winProbabilitiesFFA.get(3)),
                () -> assertEquals(0.2892937081465607, winProbabilitiesFFA.get(4))
        );
    }

    @Test
    void testPredictWinOutliers() {
        Rating r1 = new Rating();
        Rating r2 = new Rating(20.156, 8.035);
        Rating r3 = new Rating(32.444, 5.123);
        Model model = getInstance();
        Match match = new Match(List.of(
                List.of(r1),
                List.of(r2),
                List.of(r1),
                List.of(r3),
                List.of(r1)
        ));
        List<Double> winProbabilities = model.predictWin(match);
        double p1 = winProbabilities.get(0);
        double p2 = winProbabilities.get(1);
        double p3 = winProbabilities.get(3);
        assertAll(
                () -> assertNotEquals(p1, winProbabilities.get(1)),
                () -> assertNotEquals(p2, winProbabilities.get(0)),
                () -> assertNotEquals(p3, winProbabilities.get(4))
        );
        winProbabilities.sort(Double::compare);
        assertAll(
                () -> assertEquals(p1, winProbabilities.get(1)),
                () -> assertEquals(p2, winProbabilities.get(0)),
                () -> assertEquals(p3, winProbabilities.get(4))
        );
    }

    @Test
    void testPredictDraw() {
        Rating r1 = new Rating();
        Rating r2 = new Rating(32.444, 1.123);
        Rating r3 = new Rating(35.881, 0.0001);
        Rating r4 = new Rating(25.188, 0.0001);
        List<Rating> t1 = List.of(r1, r2);
        List<Rating> t2 = List.of(r3, r4);
        Model model = getInstance();

        Match match = new Match(List.of(t1, t2));
        double drawProbability = model.predictDraw(match);
        assertEquals(0.3839934595931187, drawProbability);
        match = new Match(List.of(
                t1, t2,
                List.of(r1),
                List.of(r2),
                List.of(r3)
        ));
        drawProbability = model.predictDraw(match);
        assertEquals(0.05351059864350631, drawProbability);
        match = new Match(List.of(t1, t1));
        drawProbability = model.predictDraw(match);
        assertEquals(0.3171594166053213, drawProbability);
        match = new Match(List.of(List.of(r3), List.of(r3)));
        drawProbability = model.predictDraw(match);
        assertEquals(0.9999999997530837, drawProbability);
    }

    @Test
    void testPredictRank() {
        Rating r1 = new Rating(34, 0.25);
        Rating r2 = new Rating(32, 0.25);
        Rating r3 = new Rating(30, 0.25);

        Rating r4 = new Rating(24, 0.5);
        Rating r5 = new Rating(22, 0.5);
        Rating r6 = new Rating(20, 0.5);
        List<Rating> t1 = List.of(r1, r4);
        List<Rating> t2 = List.of(r2, r5);
        List<Rating> t3 = List.of(r3, r6);
        Model model = getInstance();
        Match match = new Match(List.of(t1, t2, t3));
        List<List<Double>> rankProbabilities = model.predictRank(match);
        double rankProbabilitiesSummed = rankProbabilities.stream().map(List::getLast).reduce(0.0, Double::sum);
        double drawProbability = model.predictDraw(match);
        assertEquals(1, rankProbabilitiesSummed + drawProbability, DELTA);
        match = new Match(List.of(t1, t1, t1));
        rankProbabilities = model.predictRank(match);
        rankProbabilitiesSummed = rankProbabilities.stream().map(List::getLast).reduce(0.0, Double::sum);
        drawProbability = model.predictDraw(match);
        assertEquals(1, rankProbabilitiesSummed + drawProbability, DELTA);
    }

    @Test
    void testPredictRankOutliers() {
        Rating r1 = new Rating(30, 0.25);
        Rating r2 = new Rating(32, 0.25);
        Rating r3 = new Rating(34, 0.25);
        Model model = getInstance();
        Match match = new Match(List.of(
                List.of(r2),
                List.of(r1),
                List.of(r2),
                List.of(r3),
                List.of(r2)
        ));
        List<List<Double>> rankProbabilities = model.predictRank(match);
        double rankProbabilitiesSummed = rankProbabilities.stream().map(List::getLast).reduce(0.0, Double::sum);
        double drawProbability = model.predictDraw(match);
        double p1 = rankProbabilities.get(1).getLast();
        double p2 = rankProbabilities.get(2).getLast();
        double p3 = rankProbabilities.get(3).getLast();
        List<Double> sortedRankProbabilities = rankProbabilities.stream().map(List::getLast).sorted().toList();
        assertAll(
                () -> assertEquals(p1, sortedRankProbabilities.getFirst()),
                () -> assertEquals(p2, sortedRankProbabilities.get(1)),
                () -> assertEquals(p3, sortedRankProbabilities.getLast()),
                () -> assertEquals(1, rankProbabilitiesSummed + drawProbability, DELTA)
        );
    }

    private Model getInstance() {
        return new Model() {
            @Override
            public Match compute(Match match, List<Double> ranks) {
                throw new UnsupportedOperationException();
            }
        };
    }
}
