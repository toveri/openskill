package io.github.toveri.openskill.models;

import io.github.toveri.openskill.Match;
import org.junit.jupiter.api.Test;
import io.github.toveri.openskill.RateOptions;
import io.github.toveri.openskill.Rating;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BradleyTerryPartTest {
    @Test
    void testRate() {
        ModelOptions options = new ModelOptionsBuilder()
                .mu(10.26558503511445)
                .sigma(9.328809427064739)
                .build();
        Model model = new BradleyTerryPart(options);
        List<Rating> t1 = List.of(model.rating());
        List<Rating> t2 = List.of(model.rating(), model.rating());
        Match match = new Match(List.of(t1, t2));
        Match match1v2Default = model.rate(match);
        assertAll(
                () -> assertEquals(13.529115856530728, match1v2Default.getTeam(0).get(0).mu),
                () -> assertEquals(9.157136493268368, match1v2Default.getTeam(0).get(0).sigma),
                () -> assertEquals(7.002054213698171, match1v2Default.getTeam(1).get(0).mu),
                () -> assertEquals(9.084918843073634, match1v2Default.getTeam(1).get(0).sigma),
                () -> assertEquals(7.002054213698171, match1v2Default.getTeam(1).get(1).mu),
                () -> assertEquals(9.084918843073634, match1v2Default.getTeam(1).get(1).sigma)
        );
        Match match1v2Ranks = model.rate(match, new RateOptions(List.of(2.0, 1.0)));
        assertAll(
                () -> assertEquals(8.468876664717218, match1v2Ranks.getTeam(0).get(0).mu),
                () -> assertEquals(9.157136493268368, match1v2Ranks.getTeam(0).get(0).sigma),
                () -> assertEquals(12.062293405511681, match1v2Ranks.getTeam(1).get(0).mu),
                () -> assertEquals(9.084918843073634, match1v2Ranks.getTeam(1).get(0).sigma),
                () -> assertEquals(12.062293405511681, match1v2Ranks.getTeam(1).get(1).mu),
                () -> assertEquals(9.084918843073634, match1v2Ranks.getTeam(1).get(1).sigma)
        );
        match = new Match(List.of(t1, t2));
        Match match1v2Scores = model.rate(match, new RateOptions(List.of(1.0, 2.0), false));
        assertAll(
                () -> assertEquals(8.468876664717218, match1v2Scores.getTeam(0).get(0).mu),
                () -> assertEquals(9.157136493268368, match1v2Scores.getTeam(0).get(0).sigma),
                () -> assertEquals(12.062293405511681, match1v2Scores.getTeam(1).get(0).mu),
                () -> assertEquals(9.084918843073634, match1v2Scores.getTeam(1).get(0).sigma),
                () -> assertEquals(12.062293405511681, match1v2Scores.getTeam(1).get(1).mu),
                () -> assertEquals(9.084918843073634, match1v2Scores.getTeam(1).get(1).sigma)
        );
        List<Rating> t3 = List.of(model.rating(), model.rating(), model.rating());
        match = new Match(List.of(t1, t2, t3));
        Match match3TeamsWithDraw = model.rate(match, new RateOptions(List.of(1.0, 2.0, 1.0)));
        assertAll(
                () -> assertEquals(11.335955554350374, match3TeamsWithDraw.getTeam(0).get(0).mu),
                () -> assertEquals(9.2315523878558, match3TeamsWithDraw.getTeam(0).get(0).sigma),
                () -> assertEquals(8.72473165362405, match3TeamsWithDraw.getTeam(1).get(0).mu),
                () -> assertEquals(9.203982072532947, match3TeamsWithDraw.getTeam(1).get(0).sigma),
                () -> assertEquals(8.72473165362405, match3TeamsWithDraw.getTeam(1).get(1).mu),
                () -> assertEquals(9.203982072532947, match3TeamsWithDraw.getTeam(1).get(1).sigma),
                () -> assertEquals(10.736067897368924, match3TeamsWithDraw.getTeam(2).get(0).mu),
                () -> assertEquals(9.002955227772386, match3TeamsWithDraw.getTeam(2).get(0).sigma),
                () -> assertEquals(10.736067897368924, match3TeamsWithDraw.getTeam(2).get(1).mu),
                () -> assertEquals(9.002955227772386, match3TeamsWithDraw.getTeam(2).get(1).sigma),
                () -> assertEquals(10.736067897368924, match3TeamsWithDraw.getTeam(2).get(2).mu),
                () -> assertEquals(9.002955227772386, match3TeamsWithDraw.getTeam(2).get(2).sigma)
        );
    }
}
