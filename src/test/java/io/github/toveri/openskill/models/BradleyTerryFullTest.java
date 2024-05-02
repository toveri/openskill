package io.github.toveri.openskill.models;

import io.github.toveri.openskill.Match;
import org.junit.jupiter.api.Test;
import io.github.toveri.openskill.RateOptions;
import io.github.toveri.openskill.Rating;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BradleyTerryFullTest {
    @Test
    void testRate() {
        ModelOptions options = new ModelOptionsBuilder()
                .mu(17.842929377297303)
                .sigma(1.511049869752525)
                .build();
        Model model = new BradleyTerryFull(options);
        List<Rating> t1 = List.of(model.rating());
        List<Rating> t2 = List.of(model.rating(), model.rating());
        Match match = new Match(List.of(t1, t2));
        Match match1v2Default = model.rate(match);
        assertAll(
                () -> assertEquals(18.177036976922977, match1v2Default.getTeam(0).get(0).mu),
                () -> assertEquals(1.512801806019586, match1v2Default.getTeam(0).get(0).sigma),
                () -> assertEquals(17.508821777671628, match1v2Default.getTeam(1).get(0).mu),
                () -> assertEquals(1.5125763309974716, match1v2Default.getTeam(1).get(0).sigma),
                () -> assertEquals(17.508821777671628, match1v2Default.getTeam(1).get(1).mu),
                () -> assertEquals(1.5125763309974716, match1v2Default.getTeam(1).get(1).sigma)
        );
        Match match1v2Ranks = model.rate(match, new RateOptions(List.of(2.0, 1.0)));
        assertAll(
                () -> assertEquals(17.82192360542754, match1v2Ranks.getTeam(0).get(0).mu),
                () -> assertEquals(1.512801806019586, match1v2Ranks.getTeam(0).get(0).sigma),
                () -> assertEquals(17.863935149167066, match1v2Ranks.getTeam(1).get(0).mu),
                () -> assertEquals(1.5125763309974716, match1v2Ranks.getTeam(1).get(0).sigma),
                () -> assertEquals(17.863935149167066, match1v2Ranks.getTeam(1).get(1).mu),
                () -> assertEquals(1.5125763309974716, match1v2Ranks.getTeam(1).get(1).sigma)
        );
        match = new Match(List.of(t1, t2));
        Match match1v2Scores = model.rate(match, new RateOptions(List.of(1.0, 2.0), false));
        assertAll(
                () -> assertEquals(17.82192360542754, match1v2Scores.getTeam(0).get(0).mu),
                () -> assertEquals(1.512801806019586, match1v2Scores.getTeam(0).get(0).sigma),
                () -> assertEquals(17.863935149167066, match1v2Scores.getTeam(1).get(0).mu),
                () -> assertEquals(1.5125763309974716, match1v2Scores.getTeam(1).get(0).sigma),
                () -> assertEquals(17.863935149167066, match1v2Scores.getTeam(1).get(1).mu),
                () -> assertEquals(1.5125763309974716, match1v2Scores.getTeam(1).get(1).sigma)
        );
        List<Rating> t3 = List.of(model.rating(), model.rating(), model.rating());
        match = new Match(List.of(t1, t2, t3));
        Match match3TeamsWithDraw = model.rate(match, new RateOptions(List.of(1.0, 2.0, 1.0)));
        assertAll(
                () -> assertEquals(18.348323624601324, match3TeamsWithDraw.getTeam(0).get(0).mu),
                () -> assertEquals(1.5127608868869538, match3TeamsWithDraw.getTeam(0).get(0).sigma),
                () -> assertEquals(17.486074000410817, match3TeamsWithDraw.getTeam(1).get(0).mu),
                () -> assertEquals(1.5118317884973382, match3TeamsWithDraw.getTeam(1).get(0).sigma),
                () -> assertEquals(17.486074000410817, match3TeamsWithDraw.getTeam(1).get(1).mu),
                () -> assertEquals(1.5118317884973382, match3TeamsWithDraw.getTeam(1).get(1).sigma),
                () -> assertEquals(17.69439050687977, match3TeamsWithDraw.getTeam(2).get(0).mu),
                () -> assertEquals(1.5123636594987848, match3TeamsWithDraw.getTeam(2).get(0).sigma),
                () -> assertEquals(17.69439050687977, match3TeamsWithDraw.getTeam(2).get(1).mu),
                () -> assertEquals(1.5123636594987848, match3TeamsWithDraw.getTeam(2).get(1).sigma),
                () -> assertEquals(17.69439050687977, match3TeamsWithDraw.getTeam(2).get(2).mu),
                () -> assertEquals(1.5123636594987848, match3TeamsWithDraw.getTeam(2).get(2).sigma)
        );
    }
}
