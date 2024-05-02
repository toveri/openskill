package io.github.toveri.openskill.models;

import io.github.toveri.openskill.Match;
import org.junit.jupiter.api.Test;
import io.github.toveri.openskill.RateOptions;
import io.github.toveri.openskill.Rating;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ThurstoneMostellerPartTest {
    @Test
    void testRate() {
        ModelOptions options = new ModelOptionsBuilder()
                .mu(22.177612349590575)
                .sigma(11.187832724040407)
                .build();
        Model model = new ThurstoneMostellerPart(options);
        List<Rating> t1 = List.of(model.rating());
        List<Rating> t2 = List.of(model.rating(), model.rating());
        Match match = new Match(List.of(t1, t2));
        Match match1v2Default = model.rate(match);
        assertAll(
                () -> assertEquals(25.81146210015194, match1v2Default.getTeam(0).get(0).mu),
                () -> assertEquals(11.100692300903118, match1v2Default.getTeam(0).get(0).sigma),
                () -> assertEquals(18.54376259902921, match1v2Default.getTeam(1).get(0).mu),
                () -> assertEquals(11.064266557117664, match1v2Default.getTeam(1).get(0).sigma),
                () -> assertEquals(18.54376259902921, match1v2Default.getTeam(1).get(1).mu),
                () -> assertEquals(11.064266557117664, match1v2Default.getTeam(1).get(1).sigma)
        );
        Match match1v2Ranks = model.rate(match, new RateOptions(List.of(2.0, 1.0)));
        assertAll(
                () -> assertEquals(20.678709339425158, match1v2Ranks.getTeam(0).get(0).mu),
                () -> assertEquals(11.128957601994994, match1v2Ranks.getTeam(0).get(0).sigma),
                () -> assertEquals(23.676515359755992, match1v2Ranks.getTeam(1).get(0).mu),
                () -> assertEquals(11.104349781474195, match1v2Ranks.getTeam(1).get(0).sigma),
                () -> assertEquals(23.676515359755992, match1v2Ranks.getTeam(1).get(1).mu),
                () -> assertEquals(11.104349781474195, match1v2Ranks.getTeam(1).get(1).sigma)
        );
        match = new Match(List.of(t1, t2));
        Match match1v2Scores = model.rate(match, new RateOptions(List.of(1.0, 2.0), false));
        assertAll(
                () -> assertEquals(20.678709339425158, match1v2Scores.getTeam(0).get(0).mu),
                () -> assertEquals(11.128957601994994, match1v2Scores.getTeam(0).get(0).sigma),
                () -> assertEquals(23.676515359755992, match1v2Scores.getTeam(1).get(0).mu),
                () -> assertEquals(11.104349781474195, match1v2Scores.getTeam(1).get(0).sigma),
                () -> assertEquals(23.676515359755992, match1v2Scores.getTeam(1).get(1).mu),
                () -> assertEquals(11.104349781474195, match1v2Scores.getTeam(1).get(1).sigma)
        );
        List<Rating> t3 = List.of(model.rating(), model.rating(), model.rating());
        match = new Match(List.of(t1, t2, t3));
        Match match3TeamsWithDraw = model.rate(match, new RateOptions(List.of(1.0, 2.0, 1.0)));
        assertAll(
                () -> assertEquals(24.77003554106483, match3TeamsWithDraw.getTeam(0).get(0).mu),
                () -> assertEquals(11.108817721748975, match3TeamsWithDraw.getTeam(0).get(0).sigma),
                () -> assertEquals(20.850421823779325, match3TeamsWithDraw.getTeam(1).get(0).mu),
                () -> assertEquals(11.144648253090196, match3TeamsWithDraw.getTeam(1).get(0).sigma),
                () -> assertEquals(20.850421823779325, match3TeamsWithDraw.getTeam(1).get(1).mu),
                () -> assertEquals(11.144648253090196, match3TeamsWithDraw.getTeam(1).get(1).sigma),
                () -> assertEquals(20.912379683927572, match3TeamsWithDraw.getTeam(2).get(0).mu),
                () -> assertEquals(10.996425475486204, match3TeamsWithDraw.getTeam(2).get(0).sigma),
                () -> assertEquals(20.912379683927572, match3TeamsWithDraw.getTeam(2).get(1).mu),
                () -> assertEquals(10.996425475486204, match3TeamsWithDraw.getTeam(2).get(1).sigma),
                () -> assertEquals(20.912379683927572, match3TeamsWithDraw.getTeam(2).get(2).mu),
                () -> assertEquals(10.996425475486204, match3TeamsWithDraw.getTeam(2).get(2).sigma)
        );
    }
}
