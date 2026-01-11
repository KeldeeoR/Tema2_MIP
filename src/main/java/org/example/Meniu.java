package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class Meniu {

    public final Map<Categorie, List<Produs>> produsePeCategorii = new HashMap<>();

    // DTO pentru JSON
    private static class ProductDTO {
        String type, nume, blat, sos;
        double pret;
        Double gramaj, volum;
        List<String> toppinguri;

        ProductDTO(String type, String nume, double pret) {
            this.type = type; this.nume = nume; this.pret = pret;
        }
    }

    public Meniu() {
        // Initializam categoriile goale
        for (Categorie c : Categorie.values()) {
            produsePeCategorii.put(c, new ArrayList<>());
        }
    }

    public void adaugaProdus(Categorie cat, Produs p) {
        produsePeCategorii.computeIfAbsent(cat, k -> new ArrayList<>()).add(p);
    }

    // --- CERINȚA BAREM: Căutare cu Optional ---
    public Optional<Produs> cautaProdus(String nume) {
        return produsePeCategorii.values().stream()
                .flatMap(List::stream)
                .filter(p -> p.getNume().equalsIgnoreCase(nume))
                .findFirst();
    }

    // --- CERINȚA BAREM: Filtrare cu Streams API ---
    public List<Produs> filtreazaProduse(String cautare, Double pretMin, Double pretMax, String tip, boolean doarVegetarian) {
        return produsePeCategorii.values().stream()
                .flatMap(List::stream)
                .filter(p -> {
                    // 1. Filtru Nume
                    if (cautare != null && !cautare.isEmpty() && !p.getNume().toLowerCase().contains(cautare.toLowerCase())) {
                        return false;
                    }
                    // 2. Filtru Pret
                    if (pretMin != null && p.getPret() < pretMin) return false;
                    if (pretMax != null && p.getPret() > pretMax) return false;

                    // 3. Filtru Tip
                    if ("Mancare".equals(tip) && !(p instanceof Mancare)) return false;
                    if ("Bautura".equals(tip) && !(p instanceof Bautura)) return false;

                    // 4. Filtru Vegetarian
                    if (doarVegetarian) {
                        return (p instanceof Mancare) && ((Mancare) p).isVegetarian();
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }

    // --- IMPORT / EXPORT JSON (Pentru Admin) ---
    public boolean exportToJson(String path) {
        Map<String, List<ProductDTO>> serial = new LinkedHashMap<>();
        for (Categorie c : Categorie.values()) serial.put(c.name(), new ArrayList<>());

        for (Map.Entry<Categorie, List<Produs>> entry : produsePeCategorii.entrySet()) {
            for (Produs p : entry.getValue()) {
                ProductDTO dto;
                if (p instanceof Pizza pizza) {
                    dto = new ProductDTO("Pizza", pizza.getNume(), pizza.getPret());
                    dto.toppinguri = pizza.getToppinguri(); dto.blat = pizza.getBlat(); dto.sos = pizza.getSos();
                } else if (p instanceof Mancare m) {
                    dto = new ProductDTO("Mancare", m.getNume(), m.getPret());
                    dto.gramaj = m.getGramaj();
                } else if (p instanceof Bautura b) {
                    dto = new ProductDTO("Bautura", b.getNume(), b.getPret());
                    dto.volum = b.getVolum();
                } else {
                    dto = new ProductDTO("Produs", p.getNume(), p.getPret());
                }
                serial.get(entry.getKey().name()).add(dto);
            }
        }

        try (FileWriter fw = new FileWriter(path)) {
            new GsonBuilder().setPrettyPrinting().create().toJson(serial, fw);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Produs> importFromJson(String path) {
        List<Produs> produseImportate = new ArrayList<>();
        try (FileReader reader = new FileReader(path)) {
            Type type = new TypeToken<Map<String, List<ProductDTO>>>(){}.getType();
            Map<String, List<ProductDTO>> data = new Gson().fromJson(reader, type);
            if (data == null) return produseImportate;

            for (List<ProductDTO> dtos : data.values()) {
                for (ProductDTO dto : dtos) {
                    Produs produsReal = null;
                    if ("Pizza".equals(dto.type)) {
                        String blat = dto.blat != null ? dto.blat : "Normal";
                        String sos = dto.sos != null ? dto.sos : "Rosii";
                        Pizza.Builder builder = new Pizza.Builder(dto.nume, dto.pret, 0, false, blat, sos);
                        if (dto.toppinguri != null) dto.toppinguri.forEach(builder::adaugaTopping);
                        produsReal = builder.build();
                    } else if ("Mancare".equals(dto.type)) {
                        produsReal = new Mancare(dto.nume, dto.pret, dto.gramaj != null ? dto.gramaj : 0, false);
                    } else if ("Bautura".equals(dto.type)) {
                        produsReal = new Bautura(dto.nume, dto.pret, dto.volum != null ? dto.volum : 0);
                    }
                    if (produsReal != null) produseImportate.add(produsReal);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return produseImportate;
    }
}