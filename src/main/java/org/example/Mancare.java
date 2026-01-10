package org.example;

public non-sealed class Mancare extends Produs {
    private final double gramaj;
    private final boolean vegetarian;

    public Mancare(String nume, double pret, double gramaj, boolean vegetarian) {
        super(nume, pret);
        this.gramaj = gramaj;
        this.vegetarian = vegetarian;
    }

    public double getGramaj() {
        return gramaj;
    }

    public boolean isVegetarian() {
        return vegetarian;
    }


    @Override
    public String detalii() {
        return "Gramaj : " + gramaj + "g" + (vegetarian ? " (Vegetarian)" : "");
    }
}
