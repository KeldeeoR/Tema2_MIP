package org.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PartyPackStrategy implements OfferStrategy {
    @Override
    public double calculeazaReducere(Comanda comanda) {
        List<Double> preturiPizza = new ArrayList<>();

        for (ComandaItem item : comanda.getItems()) {
            if (item.getProdus() instanceof Pizza) {
                for (int i = 0; i < item.getCantitate(); i++) {
                    preturiPizza.add(item.getProdus().getPret());
                }
            }
        }

        if (preturiPizza.size() < 4) return 0.0;

        // Sortăm crescător (cea mai ieftină e prima)
        Collections.sort(preturiPizza);

        // Câte pizza gratis? Una la fiecare 4.
        int pizzaGratis = preturiPizza.size() / 4;

        double reducere = 0;
        for (int i = 0; i < pizzaGratis; i++) {
            reducere += preturiPizza.get(i);
        }

        return reducere;
    }

    @Override
    public String getNumeOferta() {
        return "Party Pack (1 Pizza Gratis)";
    }
}