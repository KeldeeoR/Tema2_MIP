package org.example;

import java.util.ArrayList;
import java.util.List;

public class OfferManager {
    private static OfferManager instance;
    private final List<OfferStrategy> activeOffers;

    private OfferManager() {
        this.activeOffers = new ArrayList<>();
        // Implicit putem activa o ofertă, sau le lăsăm dezactivate
        // this.activeOffers.add(new PartyPackStrategy());
    }

    public static OfferManager getInstance() {
        if (instance == null) {
            instance = new OfferManager();
        }
        return instance;
    }

    public void enableOffer(OfferStrategy strategy) {
        // Evităm duplicatele: ștergem dacă există deja un tip similar
        disableOffer(strategy.getClass());
        activeOffers.add(strategy);
    }

    public void disableOffer(Class<? extends OfferStrategy> strategyClass) {
        activeOffers.removeIf(offer -> offer.getClass().equals(strategyClass));
    }

    public List<OfferStrategy> getActiveOffers() {
        return activeOffers;
    }

    // --- ACEASTA ESTE METODA CARE ÎȚI LIPSEA ---
    public double aplicaOferte(Comanda comanda) {
        double totalReducere = 0.0;
        for (OfferStrategy strategy : activeOffers) {
            totalReducere += strategy.calculeazaReducere(comanda);
        }
        return totalReducere;
    }
}