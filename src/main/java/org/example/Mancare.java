package org.example;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("MANCARE")
public class Mancare extends Produs {
    private double gramaj;
    private boolean vegetarian;

    public Mancare() {}

    public Mancare(String nume, double pret, double gramaj, boolean vegetarian) {
        super(nume, pret);
        this.gramaj = gramaj;
        this.vegetarian = vegetarian;
    }

    public double getGramaj() { return gramaj; }
    public boolean isVegetarian() { return vegetarian; }

    @Override
    public String detalii() {
        return "Gramaj: " + gramaj + "g" + (vegetarian ? " (Veg)" : "");
    }
}