package org.toveri.openskill;

import java.util.ArrayList;
import java.util.List;

public class Match {
    private final List<List<Rating>> teams;

    public Match() {
        teams = new ArrayList<>();
    }

    public Match(int initialCapacity) {
        teams = new ArrayList<>(initialCapacity);
    }

    public Match(Match m) {
        teams = new ArrayList<>(m.teamCount());
        for (List<Rating> team : m.teams) {
            List<Rating> teamCopy = new ArrayList<>(team.size());
            for (Rating rating : team) {
                teamCopy.add(new Rating(rating));
            }
            teams.add(teamCopy);
        }
    }

    // Short for one vs one match.
    public Match(Rating rating1, Rating rating2) {
        this(List.of(List.of(rating1), List.of(rating2)));
    }

    // Short for match with only two teams.
    public Match(List<Rating> team1, List<Rating> team2) {
        this(List.of(team1, team2));
    }

    public Match(List<List<Rating>> teams) {
        this.teams = teams;
    }

    public int teamCount() {
        return teams.size();
    }

    public List<List<Rating>> getTeams() {
        return teams;
    }

    public List<Rating> getTeam(int index) {
        return teams.get(index);
    }
}
