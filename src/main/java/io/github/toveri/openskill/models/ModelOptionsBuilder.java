package io.github.toveri.openskill.models;

import io.github.toveri.openskill.Constants;
import io.github.toveri.openskill.Gamma;
import io.github.toveri.openskill.Rating;

import java.util.List;

/**
 * A builder class for creating instances of the ModelOptions class.
 * Allows setting various parameters for the model.
 */
public class ModelOptionsBuilder {
    private Double mu = null;
    private Double sigma = null;
    private Double beta = null;
    private Double kappa = null;
    private Gamma gammaFun = null;
    private Double tau = null;

    /**
     * A model options builder with no set fields.
     */
    public ModelOptionsBuilder() {}

    /**
     * Sets the value of the mean.
     *
     * @param mu The mean value.
     * @return The builder instance.
     */
    public ModelOptionsBuilder mu(double mu) {
        this.mu = mu;
        return this;
    }

    /**
     * Sets the value of the standard deviation.
     *
     * @param sigma The standard deviation value.
     * @return The builder instance.
     */
    public ModelOptionsBuilder sigma(double sigma) {
        this.sigma = sigma;
        return this;
    }

    /**
     * Sets the value of the uncertainty present in the prior distribution of ratings.
     *
     * @param beta The uncertainty value.
     * @return The builder instance.
     */
    public ModelOptionsBuilder beta(double beta) {
        this.beta = beta;
        return this;
    }

    /**
     * Sets the value of the small positive number used to prevent
     * the variance of the posterior distribution from becoming negative.
     *
     * @param kappa The kappa value.
     * @return The builder instance.
     */
    public ModelOptionsBuilder kappa(double kappa) {
        this.kappa = kappa;
        return this;
    }

    /**
     * Sets the function that controls how fast the variance is reduced.
     *
     * @param gamma The gamma function.
     * @return The builder instance.
     */
    public ModelOptionsBuilder gamma(Gamma gamma) {
        this.gammaFun = gamma;
        return this;
    }

    /**
     * Sets the value of the minimum rating variance in order to increase rating change volatility.
     *
     * @param tau The tau value.
     * @return The builder instance.
     */
    public ModelOptionsBuilder tau(double tau) {
        this.tau = tau;
        return this;
    }

    /**
     * Builds an instance with the specified parameters.
     * For any parameter is not set, the default value is used.
     *
     * @return An instance of `ModelOptions`.
     */
    public ModelOptions build() {
        mu = mu != null ? mu : Constants.MU;
        sigma = sigma != null ? sigma : Constants.SIGMA;
        beta = beta != null ? beta : Constants.BETA;
        kappa = kappa != null ? kappa : Constants.KAPPA;
        gammaFun = gammaFun != null ? gammaFun :
                (double c, int k, double mu, double sigmaSq, List<Rating> team, double rank) ->
                        Math.sqrt(sigmaSq) / c;
        tau = tau != null ? tau : Constants.TAU;
        return new ModelOptions(mu, sigma, beta, kappa, gammaFun, tau);
    }
}
