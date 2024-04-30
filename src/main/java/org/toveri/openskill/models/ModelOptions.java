package org.toveri.openskill.models;

import org.toveri.openskill.Gamma;

public record ModelOptions(
        double mu,
        double sigma,
        double beta,
        double kappa,
        Gamma gammaFun,
        double tau
) {
}
