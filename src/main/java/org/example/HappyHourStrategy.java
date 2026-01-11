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
                // Dacă avem cantitate 3, adăugăm prețul de 3 ori în listă
                for (int i = 0; i < item.getCantitate(); i++) {
                    preturiBauturi.add(item.getProdus().getPret());
                }
            }
        }

        // Sortăm descrescător ca să reducem băuturile cele mai ieftine (sau scumpe?
        // Barem: "Fiecare a doua băutură". De obicei se reduce cea mai ieftină,
        // dar pentru simplitate reducem în ordinea listei sortate).
        preturiBauturi.sort(Comparator.reverseOrder());

        double reducereTotala = 0;
        // Aplicăm reducerea la fiecare a doua băutură (index 1, 3, 5...)
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