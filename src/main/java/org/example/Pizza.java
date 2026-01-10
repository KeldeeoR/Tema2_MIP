package org.example;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;

import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("PIZZA")
public class Pizza extends Mancare {

    // Am scos 'final' pentru ca Hibernate sa poata popula campurile
    private String blat;
    private String sos;

    // @ElementCollection spune Hibernate-ului sa creeze un tabel separat (ex: Pizza_toppinguri)
    // pentru a stoca lista de string-uri.
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> toppinguri;

    // Constructor gol obligatoriu pentru JPA
    public Pizza() {
    }

    private Pizza(Builder builder) {
        super(builder.nume, builder.pret, builder.gramaj, builder.vegetarian);
        this.blat = builder.blat;
        this.sos = builder.sos;
        // Folosim new ArrayList pentru a ne asigura ca lista e mutabila (Hibernate prefera asta)
        this.toppinguri = new ArrayList<>(builder.toppinguri);
    }

    @Override
    public String detalii() {
        return "Pizza (" + blat + ", " + sos + ") - " + toppinguri;
    }

    // --- Getteri ---
    public String getBlat() {
        return blat;
    }

    public String getSos() {
        return sos;
    }

    public List<String> getToppinguri() {
        return toppinguri;
    }

    // --- Builder Pattern ---
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
}