package io.github.toveri.openskill.models;

import io.github.toveri.openskill.Match;
import org.junit.jupiter.api.Test;
import io.github.toveri.openskill.RateOptions;
import io.github.toveri.openskill.Rating;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlackettLuceTest {
    @Test
    void testRate() {
        ModelOptions options = new ModelOptionsBuilder()
                .mu(31.321989232514305)
                .sigma(11.379964801443018)
                .build();
        Model model = new PlackettLuce(options);
        List<Rating> t1 = List.of(model.rating());
        List<Rating> t2 = List.of(model.rating(), model.rating());
        Match match = new Match(List.of(t1, t2));
        Match match1v2Default = model.rate(match);
        assertAll(
                () -> assertEquals(36.48967347401391, match1v2Default.getTeam(0).get(0).mu),
                () -> assertEquals(11.237778601372977, match1v2Default.getTeam(0).get(0).sigma),
                () -> assertEquals(26.154304991014694, match1v2Default.getTeam(1).get(0).mu),
                () -> assertEquals(11.17822477763209, match1v2Default.getTeam(1).get(0).sigma),
                () -> assertEquals(26.154304991014694, match1v2Default.getTeam(1).get(1).mu),
                () -> assertEquals(11.17822477763209, match1v2Default.getTeam(1).get(1).sigma)
        );
        Match match1v2Ranks = model.rate(match, new RateOptions(List.of(2.0, 1.0)));
        assertAll(
                () -> assertEquals(30.19454401757094, match1v2Ranks.getTeam(0).get(0).mu),
                () -> assertEquals(11.237778601372977, match1v2Ranks.getTeam(0).get(0).sigma),
                () -> assertEquals(32.44943444745767, match1v2Ranks.getTeam(1).get(0).mu),
                () -> assertEquals(11.17822477763209, match1v2Ranks.getTeam(1).get(0).sigma),
                () -> assertEquals(32.44943444745767, match1v2Ranks.getTeam(1).get(1).mu),
                () -> assertEquals(11.17822477763209, match1v2Ranks.getTeam(1).get(1).sigma)
        );
        match = new Match(List.of(t1, t2));
        Match match1v2Scores = model.rate(match, new RateOptions(List.of(1.0, 2.0), false));
        assertAll(
                () -> assertEquals(30.19454401757094, match1v2Scores.getTeam(0).get(0).mu),
                () -> assertEquals(11.237778601372977, match1v2Scores.getTeam(0).get(0).sigma),
                () -> assertEquals(32.44943444745767, match1v2Scores.getTeam(1).get(0).mu),
                () -> assertEquals(11.17822477763209, match1v2Scores.getTeam(1).get(0).sigma),
                () -> assertEquals(32.44943444745767, match1v2Scores.getTeam(1).get(1).mu),
                () -> assertEquals(11.17822477763209, match1v2Scores.getTeam(1).get(1).sigma)
        );
        List<Rating> t3 = List.of(model.rating(), model.rating(), model.rating());
        match = new Match(List.of(t1, t2, t3));
        Match match3TeamsWithDraw = model.rate(match, new RateOptions(List.of(1.0, 2.0, 1.0)));
        assertAll(
                () -> assertEquals(33.21874815457553, match3TeamsWithDraw.getTeam(0).get(0).mu),
                () -> assertEquals(11.354896725884002, match3TeamsWithDraw.getTeam(0).get(0).sigma),
                () -> assertEquals(30.27713112192334, match3TeamsWithDraw.getTeam(1).get(0).mu),
                () -> assertEquals(11.291328826858104, match3TeamsWithDraw.getTeam(1).get(0).sigma),
                () -> assertEquals(30.27713112192334, match3TeamsWithDraw.getTeam(1).get(1).mu),
                () -> assertEquals(11.291328826858104, match3TeamsWithDraw.getTeam(1).get(1).sigma),
                () -> assertEquals(30.470088421044046, match3TeamsWithDraw.getTeam(2).get(0).mu),
                () -> assertEquals(11.249240904583088, match3TeamsWithDraw.getTeam(2).get(0).sigma),
                () -> assertEquals(30.470088421044046, match3TeamsWithDraw.getTeam(2).get(1).mu),
                () -> assertEquals(11.249240904583088, match3TeamsWithDraw.getTeam(2).get(1).sigma),
                () -> assertEquals(30.470088421044046, match3TeamsWithDraw.getTeam(2).get(2).mu),
                () -> assertEquals(11.249240904583088, match3TeamsWithDraw.getTeam(2).get(2).sigma)
        );
    }
}
