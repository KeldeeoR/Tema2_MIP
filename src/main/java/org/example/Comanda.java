package org.example;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Comanda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dataOra;
    private double totalPlata;

    @ManyToOne
    private User ospatar;

    @ManyToOne
    private Masa masa;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<ComandaItem> items = new ArrayList<>();

    private boolean finalizata;

    public Comanda() {
        this.dataOra = LocalDateTime.now();
        this.finalizata = false;
    }

    // --- REPARATIA PENTRU "SPAM" (Pizza x4 in loc de Pizza x1, x1, x1, x1) ---
    public void adaugaProdus(Produs produs, int cantitate) {
        // Caută dacă produsul există deja în comandă
        for (ComandaItem item : items) {
            if (item.getProdus().getId().equals(produs.getId())) {
                // Actualizează cantitatea existentă
                item.setCantitate(item.getCantitate() + cantitate);
                return;
            }
        }
        // Dacă nu există, adaugă item nou
        ComandaItem item = new ComandaItem(produs, cantitate);
        items.add(item);
    }

    public double calculeazaTotal() {
        return items.stream().mapToDouble(ComandaItem::getSubtotal).sum();
    }

    // Getteri si Setteri
    public List<ComandaItem> getItems() { return items; }
    public void setOspatar(User ospatar) { this.ospatar = ospatar; }
    public void setMasa(Masa masa) { this.masa = masa; }
    public Masa getMasa() { return masa; }
    public User getOspatar() { return ospatar; }
    public boolean isFinalizata() { return finalizata; }
    public void setFinalizata(boolean finalizata) { this.finalizata = finalizata; }
    public void setTotalPlata(double total) { this.totalPlata = total; }
    public Long getId() { return id; }
    public LocalDateTime getDataOra() { return dataOra; }
    public double getTotalPlata() { return totalPlata; }
}