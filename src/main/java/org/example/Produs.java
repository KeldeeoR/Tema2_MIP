package org.example;

public sealed abstract class Produs permits Mancare, Bautura {
    protected String nume;
    protected double pret;

    public Produs(String nume, double pret) {
        this.nume = nume;
        this.pret = pret;
    }

    public String getNume() {
        return nume;
    }

    public double getPret() {
        return pret;
    }

    public abstract String detalii();

    @Override
    public String toString() {
        return nume + " − " + pret + " RON";
    }
}
