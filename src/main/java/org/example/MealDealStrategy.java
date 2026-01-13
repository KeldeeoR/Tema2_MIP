package org.example;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MealDealStrategy implements OfferStrategy {
    @Override
    public double calculeazaReducere(Comanda comanda) {
        boolean arePizza = comanda.getItems().stream()
                .anyMatch(i -> i.getProdus() instanceof Pizza);

        if (!arePizza) return 0.0;

        List<Double> preturiDesert = new ArrayList<>();
        for (ComandaItem item : comanda.getItems()) {
            String nume = item.getProdus().getNume().toLowerCase();
            if (nume.contains("tiramisu") || nume.contains("clatite") || nume.contains("cheesecake")) {
                for (int i = 0; i < item.getCantitate(); i++) {
                    preturiDesert.add(item.getProdus().getPret());
                }
            }
        }

        if (preturiDesert.isEmpty()) return 0.0;

        double celMaiIeftinDesert = preturiDesert.stream().min(Double::compare).get();
        return celMaiIeftinDesert * 0.25;
    }

    @Override
    public String getNumeOferta() {
        return "Meal Deal (-25% Desert)";
    }
}