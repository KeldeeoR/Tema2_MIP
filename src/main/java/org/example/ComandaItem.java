package org.example;

import jakarta.persistence.*;

@Entity
public class ComandaItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "produs_id")
    private Produs produs;

    private int cantitate;

    public ComandaItem() {}

    public ComandaItem(Produs produs, int cantitate) {
        this.produs = produs;
        this.cantitate = cantitate;
    }

    public Produs getProdus() { return produs; }
    public int getCantitate() { return cantitate; }
    public void setCantitate(int cantitate) { this.cantitate = cantitate; }

    public double getSubtotal() {
        return produs.getPret() * cantitate;
    }

    @Override
    public String toString() {
        return produs.getNume() + " x " + cantitate + " = " + getSubtotal() + " RON";
    }
}