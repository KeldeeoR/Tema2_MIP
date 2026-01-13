package org.example;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class HappyHourStrategy implements OfferStrategy {
    @Override
    public double calculeazaReducere(Comanda comanda) {
        List<Double> preturiBauturi = new ArrayList<>();

        for (ComandaItem item : comanda.getItems()) {
            if (item.getProdus() instanceof Bautura) {
                for (int i = 0; i < item.getCantitate(); i++) {
                    preturiBauturi.add(item.getProdus().getPret());
                }
            }
        }
        preturiBauturi.sort(Comparator.reverseOrder());

        double reducereTotala = 0;
        for (int i = 1; i < preturiBauturi.size(); i += 2) {
            reducereTotala += preturiBauturi.get(i) * 0.5;
        }

        return reducereTotala;
    }

    @Override
    public String getNumeOferta() {
        return "Happy Hour (50% la a 2-a băutură)";
    }
}