package io.github.toveri.openskill;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static io.github.toveri.openskill.Common.*;

class CommonTest {
    static final double DELTA = 1e-15;

    @Test
    void testUnwindNone() {
        List<Object> values = List.of();
        List<Double> ranks = List.of();
        Pair<List<Object>, List<Double>> result = unwind(values, ranks);
        assertAll(
                () -> assertEquals(List.of(), result.a()),
                () -> assertEquals(List.of(), result.b())
        );
    }

    @Test
    void testUnwindOne() {
        List<Object> values = List.of("foo");
        List<Double> ranks = List.of(0.0);
        Pair<List<Object>, List<Double>> result = unwind(values, ranks);
        assertAll(
                () -> assertEquals(List.of("foo"), result.a()),
                () -> assertEquals(List.of(0.0), result.b())
        );
    }

    @Test
    void testUnwindTwo() {
        List<Object> values = List.of("foo", "bar");
        List<Double> ranks = List.of(1.0, 0.0);
        Pair<List<Object>, List<Double>> result = unwind(values, ranks);
        assertAll(
                () -> assertEquals(List.of("bar", "foo"), result.a()),
                () -> assertEquals(List.of(1.0, 0.0), result.b())
        );
    }

    @Test
    void testUnwindThree() {
        List<Object> values = List.of("foo", "bar", "baz");
        List<Double> ranks = List.of(1.0, 2.0, 0.0);
        Pair<List<Object>, List<Double>> result = unwind(values, ranks);
        assertAll(
                () -> assertEquals(List.of("baz", "foo", "bar"), result.a()),
                () -> assertEquals(List.of(2.0, 0.0, 1.0), result.b())
        );
    }

    @Test
    void testUnwindUndo() {
        List<Object> values = List.of("foo", "bar", "baz");
        List<Double> ranks = List.of(1.0, 2.0, 0.0);
        Pair<List<Object>, List<Double>> result = unwind(values, ranks);
        Pair<List<Object>, List<Double>> resultUndone = unwind(result.a(), result.b());
        assertAll(
                () -> assertEquals(values, resultUndone.a()),
                () -> assertEquals(ranks, resultUndone.b()),
                () -> assertNotEquals(values, result.a()),
                () -> assertNotEquals(ranks, result.b())
        );
    }

    @Test
    void testLadderPairs() {
        assertAll(
                () -> assertEquals(List.of(List.of()), ladderPairs(List.of())),
                () -> assertEquals(List.of(List.of()), ladderPairs(List.of(1))),
                () -> assertEquals(List.of(List.of(2), List.of(1)), ladderPairs(List.of(1, 2))),
                () -> assertEquals(List.of(List.of(2), List.of(1, 3), List.of(2)), ladderPairs(List.of(1, 2, 3))),
                () -> assertEquals(List.of(List.of(2), List.of(1, 3), List.of(2, 4), List.of(3)), ladderPairs(List.of(1, 2, 3, 4)))
        );
    }

    @Test
    void testV() {
        assertAll(
                () -> assertEquals(1.5251352761609815, v(1.0, 2.0)),
                () -> assertEquals(2.373215532822845, v(0.0, 2.0)),
                () -> assertEquals(0.28759997093917833, v(0.0, -1.0)),
                () -> assertEquals(5.186503967125839, v(0.0, 5.0)),
                () -> assertEquals(10.09809323396246, v(0.0, 10.0))
        );
    }

    @Test
    void testVt() {
        assertAll(
                () -> assertEquals(1100, vt(-1000, -100)),
                () -> assertEquals(-1100, vt(1000, -100)),
                () -> assertEquals(0.7978845608028654, vt(-1000, 1000)),
                () -> assertEquals(0, vt(0, 1000))
        );
    }

    @Test
    void testW() {
        assertAll(
                () -> assertEquals(0.8009023344296519, w(1, 2)),
                () -> assertEquals(0.8857208995859301, w(0, 2)),
                () -> assertEquals(0.37031371422339454, w(0, -1)),
                () -> assertEquals(0.9905546221738191, w(0, 10)),
                () -> assertEquals(0.9921193184077092, w(-1, 10))
        );
    }

    @Test
    void testWt() {
        assertAll(
                () -> assertEquals(0.38385826464217065, wt(1, 2)),
                () -> assertEquals(0.22625869645007676, wt(0, 2)),
                () -> assertEquals(1, wt(0, -1)),
                () -> assertEquals(1, wt(0, 0)),
                () -> assertEquals(0, wt(0, 10), DELTA)
        );
    }
}
