package io.github.toveri.openskill;

/**
 * Representation of a rating, used as a place-in for things like players.
 */
public class Rating {
    /**
     * The mean value.
     */
    public double mu;
    /**
     * The standard deviation.
     */
    public double sigma;

    /**
     * Create a rating with the constant default values.
     * Note that these values might be different from the default values of any given model.
     */
    public Rating() {
        this(Constants.MU, Constants.SIGMA);
    }

    /**
     * Create a copy of a rating.
     * @param r The rating to copy.
     */
    public Rating(Rating r) {
        this(r.mu, r.sigma);
    }

    /**
     * Create a rating with a set mean value, and constant default standard deviation value.
     * @param mu The mean value.
     */
    public Rating(double mu) {
        this(mu, Constants.SIGMA);
    }

    /**
     * Create a rating with the given mean and standard deviation values.
     * @param mu The mean value.
     * @param sigma The standard deviation value.
     */
    public Rating(double mu, double sigma) {
        this.mu = mu;
        this.sigma = sigma;
    }

    /**
     * Compare if two ratings have the same mu and sigma values.
     * @param o The object to compare with.
     * @return If the two ratings are equivalent.
     */
    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Rating r)) {
            return false;
        }
        return mu == r.mu && sigma == r.sigma;
    }

    /**
     * Return the ordinal of the rating.
     * Useful for representing its value in one number for sorting or display purposes.
     * @return The ordinal value of the rating.
     */
    public double ordinal() {
        return mu - Constants.Z * sigma;
    }
}
