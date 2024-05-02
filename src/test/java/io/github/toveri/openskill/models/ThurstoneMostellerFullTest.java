package io.github.toveri.openskill.models;

import io.github.toveri.openskill.Match;
import org.junit.jupiter.api.Test;
import io.github.toveri.openskill.RateOptions;
import io.github.toveri.openskill.Rating;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ThurstoneMostellerFullTest {
    @Test
    void testRate() {
        ModelOptions options = new ModelOptionsBuilder()
                .mu(22.919853696612385)
                .sigma(7.315441649030257)
                .build();
        Model model = new ThurstoneMostellerFull(options);
        List<Rating> t1 = List.of(model.rating());
        List<Rating> t2 = List.of(model.rating(), model.rating());
        Match match = new Match(List.of(t1, t2));
        Match match1v2Default = model.rate(match);
        assertAll(
                () -> assertEquals(30.80436898362278, match1v2Default.getTeam(0).get(0).mu),
                () -> assertEquals(6.848823647647466, match1v2Default.getTeam(0).get(0).sigma),
                () -> assertEquals(15.03533840960199, match1v2Default.getTeam(1).get(0).mu),
                () -> assertEquals(6.645738987776486, match1v2Default.getTeam(1).get(0).sigma),
                () -> assertEquals(15.03533840960199, match1v2Default.getTeam(1).get(1).mu),
                () -> assertEquals(6.645738987776486, match1v2Default.getTeam(1).get(1).sigma)
        );
        Match match1v2Ranks = model.rate(match, new RateOptions(List.of(2.0, 1.0)));
        assertAll(
                () -> assertEquals(22.50057782249884, match1v2Ranks.getTeam(0).get(0).mu),
                () -> assertEquals(7.214694670332423, match1v2Ranks.getTeam(0).get(0).sigma),
                () -> assertEquals(23.33912957072593, match1v2Ranks.getTeam(1).get(0).mu),
                () -> assertEquals(7.172348917871497, match1v2Ranks.getTeam(1).get(0).sigma),
                () -> assertEquals(23.33912957072593, match1v2Ranks.getTeam(1).get(1).mu),
                () -> assertEquals(7.172348917871497, match1v2Ranks.getTeam(1).get(1).sigma)
        );
        match = new Match(List.of(t1, t2));
        Match match1v2Scores = model.rate(match, new RateOptions(List.of(1.0, 2.0), false));
        assertAll(
                () -> assertEquals(22.50057782249884, match1v2Scores.getTeam(0).get(0).mu),
                () -> assertEquals(7.214694670332423, match1v2Scores.getTeam(0).get(0).sigma),
                () -> assertEquals(23.33912957072593, match1v2Scores.getTeam(1).get(0).mu),
                () -> assertEquals(7.172348917871497, match1v2Scores.getTeam(1).get(0).sigma),
                () -> assertEquals(23.33912957072593, match1v2Scores.getTeam(1).get(1).mu),
                () -> assertEquals(7.172348917871497, match1v2Scores.getTeam(1).get(1).sigma)
        );
        List<Rating> t3 = List.of(model.rating(), model.rating(), model.rating());
        match = new Match(List.of(t1, t2, t3));
        Match match3TeamsWithDraw = model.rate(match, new RateOptions(List.of(1.0, 2.0, 1.0)));
        assertAll(
                () -> assertEquals(40.6650444553867, match3TeamsWithDraw.getTeam(0).get(0).mu),
                () -> assertEquals(6.447206062682213, match3TeamsWithDraw.getTeam(0).get(0).sigma),
                () -> assertEquals(14.466950192459162, match3TeamsWithDraw.getTeam(1).get(0).mu),
                () -> assertEquals(6.526969667932674, match3TeamsWithDraw.getTeam(1).get(0).sigma),
                () -> assertEquals(14.466950192459162, match3TeamsWithDraw.getTeam(1).get(1).mu),
                () -> assertEquals(6.526969667932674, match3TeamsWithDraw.getTeam(1).get(1).sigma),
                () -> assertEquals(13.627566441991291, match3TeamsWithDraw.getTeam(2).get(0).mu),
                () -> assertEquals(6.508263154659147, match3TeamsWithDraw.getTeam(2).get(0).sigma),
                () -> assertEquals(13.627566441991291, match3TeamsWithDraw.getTeam(2).get(1).mu),
                () -> assertEquals(6.508263154659147, match3TeamsWithDraw.getTeam(2).get(1).sigma),
                () -> assertEquals(13.627566441991291, match3TeamsWithDraw.getTeam(2).get(2).mu),
                () -> assertEquals(6.508263154659147, match3TeamsWithDraw.getTeam(2).get(2).sigma)
        );
    }
}
