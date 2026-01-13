package org.example;

import java.util.ArrayList;
import java.util.List;

public class OfferManager {
    private static OfferManager instance;
    private final List<OfferStrategy> activeOffers;

    private OfferManager() {
        this.activeOffers = new ArrayList<>();
    }

    public static OfferManager getInstance() {
        if (instance == null) {
            instance = new OfferManager();
        }
        return instance;
    }

    public void enableOffer(OfferStrategy strategy) {
        disableOffer(strategy.getClass());
        activeOffers.add(strategy);
    }

    public void disableOffer(Class<? extends OfferStrategy> strategyClass) {
        activeOffers.removeIf(offer -> offer.getClass().equals(strategyClass));
    }

    public List<OfferStrategy> getActiveOffers() {
        return activeOffers;
    }

    public double aplicaOferte(Comanda comanda) {
        double totalReducere = 0.0;
        for (OfferStrategy strategy : activeOffers) {
            totalReducere += strategy.calculeazaReducere(comanda);
        }
        return totalReducere;
    }
}