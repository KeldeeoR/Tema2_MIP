package org.example;

import jakarta.persistence.*;

@Entity
public class Masa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private int numarMasa;

    private boolean esteOcupata;

    public Masa() {}

    public Masa(int numarMasa) {
        this.numarMasa = numarMasa;
        this.esteOcupata = false;
    }

    public Long getId() { return id; }
    public int getNumarMasa() { return numarMasa; }
    public boolean isEsteOcupata() { return esteOcupata; }
    public void setEsteOcupata(boolean esteOcupata) { this.esteOcupata = esteOcupata; }

    @Override
    public String toString() { return "Masa " + numarMasa + (esteOcupata ? " (Ocupat)" : " (Liber)"); }
}