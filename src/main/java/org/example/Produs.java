package org.example;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tip_produs", discriminatorType = DiscriminatorType.STRING)
public abstract class Produs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    protected String nume;
    protected double pret;

    public Produs() {}

    public Produs(String nume, double pret) {
        this.nume = nume;
        this.pret = pret;
    }

    public Long getId() { return id; }
    public String getNume() { return nume; }
    public double getPret() { return pret; }
    public void setPret(double pret) { this.pret = pret; }

    public abstract String detalii();

    @Override
    public String toString() {
        return nume + " - " + pret + " RON";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Produs produs = (Produs) o;
        return Objects.equals(id, produs.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}