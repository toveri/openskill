package io.github.toveri.openskill.models;

import io.github.toveri.openskill.Gamma;


/**
 * Options object for a rating model.
 *
 * @param mu       The default mean value.
 * @param sigma    The default standard deviation.
 * @param beta     The uncertainty value.
 * @param kappa    The value to prevent negative posterior distributions.
 * @param gammaFun The function that controls how fast the variance is reduced.
 * @param tau      The minimum rating variance value.
 */
public record ModelOptions(
        double mu,
        double sigma,
        double beta,
        double kappa,
        Gamma gammaFun,
        double tau
) {
}
