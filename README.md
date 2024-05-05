Java implementation of the online ranking algorithms described in https://www.csie.ntu.edu.tw/~cjlin/papers/online_ranking/online_journal.pdf

This is a hobby project.

## Installation

To add a dependency using Maven Central, simply add it to your pom.xml:

```xml
<dependency>
    <groupId>io.github.toveri</groupId>
    <artifactId>openskill</artifactId>
    <version>1.0.0</version>
</dependency>
```

Alternatively build it from the source files.

```shell
git clone https://github.com/toveri/openskill
cd openskill
mvn install
```

## Usage

The Plackett-Luce model is a good general model, but others are available as well. All models default to reasonable settings.
```java
Model model = new PlackettLuce();
```

Customize model settings as you want.
```java
ModelOptions modelOptions = new ModelOptionsBuilder()
        .mu(25)
        .sigma(8.333)
        .beta(4.167)
        .tau(0.083)
        .build();
Model customModel = new PlackettLuce(modelOptions);
```

Think of ratings as the stand-ins for players, or whatever you are interested in ranking. Create ratings based on the defaults from the model.
```java
Rating rating = model.rating();
```

Teams are just lists of one or more ratings.
```java
List<Rating> team1 = List.of(model.rating());
```

Asymmetric teams are supported, as well as any number of teams.
```java
List<Rating> team2 = List.of(model.rating());
List<Rating> team3 = List.of(model.rating(), model.rating());
```

A match is basically a list of teams.
```java
Match match = new Match(List.of(team1, team2, team3));
// There is a convenient way to create one vs one matches.
// This is just two teams of one rating each.
match = new Match(model.rating(), model.rating());
// There is similar way for matches with only two teams.
match = new Match(team1, team2);
```

Unless specified, teams are assumed to be listed in first -> last place order, as in best -> worst rank.
```java
Match ratedMatch = model.rate(match);
```

Specify a custom ranking order.
```java
List<Double> ranks = List.of(3.0, 1.0);
RateOptions rateOptions = new RateOptions(ranks);
ratedMatch = model.rate(match, rateOptions);
```

Rank by scores (higher is better) instead of ranks (lower is better). For ties, provide identical ranks or scores.
```java
List<Double> scores = List.of(147.4, 1323.5);
rateOptions = new RateOptions(scores, false);
ratedMatch = model.rate(match, rateOptions);
```

Get the estimated probability for each team winning.
```java
List<Double> winProbabilities = model.predictWin(match);
```

Get the probability the match will end in a draw.
```java
double drawProbability = model.predictDraw(match);
```

Get an estimate for the results of the match, as in the most likely rank for each team, and the probability for that rank.
```java
List<List<Double>> rankProbabilities = model.predictRank(match);
```

For purposes of sorting or displaying ratings in one value, use their ordinals.
```java
rating.ordinal();
```
