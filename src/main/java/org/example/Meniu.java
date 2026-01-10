package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

public class Meniu {

    public final Map<Categorie, List<Produs>> produsePeCategorii = new HashMap<>();

    // DTO(Data Transfer Object) simplu pentru serializare
    private static class ProductDTO {
        String type;
        String nume;
        double pret;
        Double gramaj;  // nullable
        Double volum;   // nullable
        List<String> toppinguri;  // nullable
        String blat;
        String sos;

        ProductDTO(String type, String nume, double pret) {
            this.type = type;
            this.nume = nume;
            this.pret = pret;
        }
    }

    /**
     * Exporta meniul curent intr-un fișier JSON la path-ul specificat.
     * Returneaza true daca exportul a reusit, false altfel.
     */
    public boolean exportToJson(String path) {
        // Construim o mapa: categorie -> lista de produse DTO
        Map<String, List<ProductDTO>> serial = new LinkedHashMap<>();
        for (Categorie c : Categorie.values()) {
            serial.put(c.name(), new ArrayList<>());
        }

        // Parcurgem produsele și le adaugam în DTO
        for (Map.Entry<Categorie, List<Produs>> entry : produsePeCategorii.entrySet()) {
            for (Produs p : entry.getValue()) {
                ProductDTO dto;

                // Daca produsul este o Pizza
                if (p instanceof Pizza pizza) {
                    dto = new ProductDTO("Pizza", pizza.getNume(), pizza.getPret());
                    dto.toppinguri = pizza.getToppinguri();
                    dto.blat = pizza.getBlat();
                    dto.sos = pizza.getSos();
                    dto.gramaj = null;
                    dto.volum = null;
                }
                // Daca produsul este o Mancare
                else if (p instanceof Mancare m) {
                    dto = new ProductDTO("Mancare", m.getNume(), m.getPret());
                    dto.gramaj = m.getGramaj();
                    dto.volum = null;
                    dto.toppinguri = null;
                }
                // Daca produsul este o Bautura
                else if (p instanceof Bautura b) {
                    dto = new ProductDTO("Bautura", b.getNume(), b.getPret());
                    dto.volum = b.getVolum();
                    dto.gramaj = null;
                    dto.toppinguri = null;
                }
                // Dacă este alt tip de produs (inclusiv Produs generic)
                else {
                    dto = new ProductDTO("Produs", p.getNume(), p.getPret());
                    dto.gramaj = null;
                    dto.volum = null;
                    dto.toppinguri = null;
                }

                // Adaugam DTO-ul la categoria corespunzatoare
                serial.get(entry.getKey().name()).add(dto);
            }
        }

        // Cream un obiect Gson pentru serializare (pentru a obține un JSON frumos)
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try (FileWriter fw = new FileWriter(path)) {
            // Scriem meniul serializat în fișier
            gson.toJson(serial, fw);
            return true;  // Exportul a reușit
        } catch (IOException e) {
            System.err.println("Eroare la export JSON: " + e.getMessage());
            return false;  // A aparut o eroare la salvarea fisierului
        }
    }

    /**
     * Importă produse dintr-un fișier JSON.
     * Returnează o listă de obiecte Produs (Mancare/Bautura/Pizza) create din JSON.
     */
    public List<Produs> importFromJson(String path) {
        List<Produs> produseImportate = new ArrayList<>();
        Gson gson = new Gson();

        try (FileReader reader = new FileReader(path)) {
            // 1. Definim tipul de date din JSON: Map<String, List<ProductDTO>>
            Type type = new TypeToken<Map<String, List<ProductDTO>>>(){}.getType();
            Map<String, List<ProductDTO>> data = gson.fromJson(reader, type);

            if (data == null) return produseImportate;

            // 2. Iterăm prin categorii și convertim DTO -> Obiecte Reale
            for (List<ProductDTO> dtos : data.values()) {
                for (ProductDTO dto : dtos) {
                    Produs produsReal = null;

                    if ("Pizza".equals(dto.type)) {
                        // Reconstruim Pizza folosind Builder
                        // Atenție: JSON-ul s-ar putea să aibă null la unele câmpuri, le tratăm
                        String blat = dto.blat != null ? dto.blat : "Normal";
                        String sos = dto.sos != null ? dto.sos : "Rosii";

                        Pizza.Builder builder = new Pizza.Builder(dto.nume, dto.pret, 0, false, blat, sos);
                        if (dto.toppinguri != null) {
                            for (String t : dto.toppinguri) builder.adaugaTopping(t);
                        }
                        produsReal = builder.build();
                    }
                    else if ("Mancare".equals(dto.type)) {
                        produsReal = new Mancare(dto.nume, dto.pret, dto.gramaj != null ? dto.gramaj : 0, false);
                    }
                    else if ("Bautura".equals(dto.type)) {
                        produsReal = new Bautura(dto.nume, dto.pret, dto.volum != null ? dto.volum : 0);
                    }

                    if (produsReal != null) {
                        produseImportate.add(produsReal);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Eroare la import: " + e.getMessage());
            e.printStackTrace();
        }
        return produseImportate;
    }

    // Metoda pentru adaugarea unui produs în Meniu
    public void adaugaProdus(Categorie categorie, Produs produs) {
        produsePeCategorii
                .computeIfAbsent(categorie, k -> new ArrayList<>())  // Creeaza o lista daca nu exista deja
                .add(produs);  // Adauga produsul în categoria respectiva
    }

    public List<Produs> getProduseVegetarieneSortate() {
        return produsePeCategorii.values().stream()
                .flatMap(List::stream)
                .filter(p -> p instanceof Mancare && ((Mancare) p).isVegetarian())
                .sorted((a, b) -> a.getNume().compareToIgnoreCase(b.getNume()))
                .toList();
    }

    public double getPretMediuDeserturi() {
        if (!produsePeCategorii.containsKey(Categorie.DESERT)) return 0.0;
        return produsePeCategorii.get(Categorie.DESERT).stream()
                .mapToDouble(Produs::getPret)
                .average()
                .orElse(0);
    }

    public boolean existaProdusePeste100() {
        return produsePeCategorii.values().stream()
                .flatMap(List::stream)
                .anyMatch(p -> p.getPret() > 100);
    }

    public Optional<Produs> cautaProdus(String nume) {
        return produsePeCategorii.values().stream()
                .flatMap(List::stream)
                .filter(p -> p.getNume().equalsIgnoreCase(nume))
                .findFirst();
    }
}