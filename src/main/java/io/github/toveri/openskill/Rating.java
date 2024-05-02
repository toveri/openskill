package io.github.toveri.openskill;

public class Rating {
    public double mu;
    public double sigma;

    public Rating() {
        this(Constants.MU, Constants.SIGMA);
    }

    public Rating(Rating r) {
        this(r.mu, r.sigma);
    }

    public Rating(double mu) {
        this(mu, Constants.SIGMA);
    }

    public Rating(double mu, double sigma) {
        this.mu = mu;
        this.sigma = sigma;
    }

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

    public double ordinal() {
        return mu - Constants.Z * sigma;
    }
}
