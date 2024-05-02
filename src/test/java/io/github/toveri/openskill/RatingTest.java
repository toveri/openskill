package io.github.toveri.openskill;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static io.github.toveri.openskill.Constants.*;

public class RatingTest {
    @Test
    void testDefaults() {
        Rating r = new Rating();
        assertAll(
                () -> assertEquals(MU, r.mu),
                () -> assertEquals(SIGMA, r.sigma)
        );
    }

    @Test
    void testConstructing() {
        Rating r1 = new Rating();
        r1.mu = 10;
        r1.sigma = 1;
        Rating r2 = new Rating(r1);
        Rating r3 = new Rating(10, 1);
        Rating r4 = new Rating(10);
        assertAll(
                () -> assertEquals(10, r2.mu),
                () -> assertEquals(1, r2.sigma),
                () -> assertEquals(10, r3.mu),
                () -> assertEquals(1, r3.sigma),
                () -> assertEquals(10, r4.mu),
                () -> assertEquals(SIGMA, r4.sigma)
        );
    }

    @Test
    void testOrdinal() {
        Rating r1 = new Rating();
        Rating r2 = new Rating(30, 10);
        assertAll(
                () -> assertEquals(MU - Z * SIGMA, r1.ordinal()),
                () -> assertEquals(MU - Z * SIGMA, r2.ordinal())
        );
    }

    @Test
    void testEquality() {
        Rating r1 = new Rating();
        Rating r2 = new Rating();
        Rating r3 = new Rating(1);
        assertAll(
                () -> assertEquals(r1, r2),
                () -> assertNotEquals(r1, r3)
        );
    }
}
