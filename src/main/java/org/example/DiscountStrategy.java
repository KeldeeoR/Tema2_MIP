package org.example;

@FunctionalInterface
public interface DiscountStrategy {
    double aplicaDiscount(double totalFaraTVA,
                          java.util.Map<Produs, Integer> produse);
}
