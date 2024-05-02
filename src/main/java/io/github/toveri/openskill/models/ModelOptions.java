package io.github.toveri.openskill.models;

import io.github.toveri.openskill.Gamma;

public record ModelOptions(
        double mu,
        double sigma,
        double beta,
        double kappa,
        Gamma gammaFun,
        double tau
) {
}
