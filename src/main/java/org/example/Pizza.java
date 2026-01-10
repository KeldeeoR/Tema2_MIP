package org.example;

import java.util.ArrayList;
import java.util.List;

public final class Pizza extends Mancare {

    private final String blat;
    private final String sos;
    private final List<String> toppinguri;

    private Pizza(Builder builder) {
        super(builder.nume, builder.pret, builder.gramaj, builder.vegetarian);
        this.blat = builder.blat;
        this.sos = builder.sos;
        this.toppinguri = List.copyOf(builder.toppinguri);
    }

    @Override
    public String detalii() {
        return "Blat: " + blat + ", Sos: " + sos + ", Toppinguri: " + toppinguri;
    }

    public static class Builder {

        private final String nume;
        private final double pret;
        private final double gramaj;
        private final boolean vegetarian;

        private final String blat;
        private final String sos;

        private final List<String> toppinguri = new ArrayList<>();

        public Builder(String nume,
                       double pret,
                       double gramaj,
                       boolean vegetarian,
                       String blat,
                       String sos) {
            this.nume = nume;
            this.pret = pret;
            this.gramaj = gramaj;
            this.vegetarian = vegetarian;
            this.blat = blat;
            this.sos = sos;
        }

        public Builder adaugaTopping(String topping) {
            this.toppinguri.add(topping);
            return this;
        }

        public Pizza build() {
            return new Pizza(this);
        }
    }

    public String getBlat() {
        return blat;
    }

    public String getSos() {
        return sos;
    }

    public List<String> getToppinguri() {
        return toppinguri;
    }


}
