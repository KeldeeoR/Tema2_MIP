package org.example;

public final class Bautura extends Produs {
    private final double volum;

    public Bautura(String nume, double pret, double volum) {
        super(nume, pret);
        this.volum = volum;
    }

    public double getVolum() {
        return volum;
    }


    @Override
    public String detalii() {
        return "Volum : " + volum + "ml";
    }
}
