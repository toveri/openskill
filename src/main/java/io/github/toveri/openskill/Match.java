package io.github.toveri.openskill;

import java.util.ArrayList;
import java.util.List;

/**
 * Representation of a match between teams.
 */
public class Match {
    private final List<List<Rating>> teams;

    /**
     * Create a match without any teams.
     */
    public Match() {
        teams = new ArrayList<>();
    }

    /**
     * Create a match with a set initial capacity of teams.
     * @param initialCapacity Count of teams allocated for.
     */
    public Match(int initialCapacity) {
        teams = new ArrayList<>(initialCapacity);
    }

    /**
     * Create a match that is a deep copy of the given match.
     * @param m The match to copy.
     */
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

    /**
     * Create a match with two ratings.
     * Short for one vs one match, as in two teams with one rating each.
     * @param rating1 The first rating to add.
     * @param rating2 The second rating to add.
     */
    public Match(Rating rating1, Rating rating2) {
        this(List.of(List.of(rating1), List.of(rating2)));
    }

    /**
     * Create a match with two teams.
     * Short for match with exactly two teams.
     * @param team1 The first team to add.
     * @param team2 The second team to add.
     */
    public Match(List<Rating> team1, List<Rating> team2) {
        this(List.of(team1, team2));
    }

    /**
     * Create a match with the given list of teams.
     * @param teams The list of teams.
     */
    public Match(List<List<Rating>> teams) {
        this.teams = teams;
    }

    /**
     * Get the number of teams in the match.
     * @return The count of teams.
     */
    public int teamCount() {
        return teams.size();
    }

    /**
     * Get the list of teams in the match.
     * @return The list of teams.
     */
    public List<List<Rating>> getTeams() {
        return teams;
    }

    /**
     * Get the team at the given index.
     * @param index The index of the team to get.
     * @return The team (list of ratings).
     */
    public List<Rating> getTeam(int index) {
        return teams.get(index);
    }
}
