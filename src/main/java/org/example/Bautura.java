package org.example;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("BAUTURA")
public class Bautura extends Produs {
    private double volum;

    public Bautura() {} // Constructor gol

    public Bautura(String nume, double pret, double volum) {
        super(nume, pret);
        this.volum = volum;
    }

    public double getVolum() { return volum; }

    @Override
    public String detalii() {
        return "Volum: " + volum + "ml";
    }
}