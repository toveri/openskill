package org.toveri.openskill.models;

import org.toveri.openskill.Constants;
import org.toveri.openskill.Gamma;
import org.toveri.openskill.Rating;

import java.util.List;

public class ModelOptionsBuilder {
    private Double mu = null;
    private Double sigma = null;
    private Double beta = null;
    private Double kappa = null;
    private Gamma gammaFun = null;
    private Double tau = null;

    public ModelOptionsBuilder mu(double mu) {
        this.mu = mu;
        return this;
    }

    public ModelOptionsBuilder sigma(double sigma) {
        this.sigma = sigma;
        return this;
    }

    public ModelOptionsBuilder beta(double beta) {
        this.beta = beta;
        return this;
    }

    public ModelOptionsBuilder kappa(double kappa) {
        this.kappa = kappa;
        return this;
    }

    public ModelOptionsBuilder gamma(Gamma gamma) {
        this.gammaFun = gamma;
        return this;
    }

    public ModelOptionsBuilder tau(double tau) {
        this.tau = tau;
        return this;
    }

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
