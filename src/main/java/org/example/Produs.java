package org.example;

import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tip_produs", discriminatorType = DiscriminatorType.STRING)
public abstract class Produs { // Am scos 'sealed' momentan pentru a simplifica JPA, sau il poti pastra dar Hibernate uneori se plange

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id; // Avem nevoie de un ID pentru baza de date

    protected String nume;
    protected double pret;

    // Constructor gol obligatoriu pentru JPA/Hibernate
    public Produs() {}

    public Produs(String nume, double pret) {
        this.nume = nume;
        this.pret = pret;
    }

    public Long getId() { return id; }
    public String getNume() { return nume; }
    public double getPret() { return pret; }

    // Setter necesar pentru update pret din GUI
    public void setPret(double pret) { this.pret = pret; }

    public abstract String detalii();

    @Override
    public String toString() {
        return nume + " - " + pret + " RON";
    }
}