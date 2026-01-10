package org.example;

import java.util.Map;

public class Main {
    public static void main(String[] args) {

        // =============================================================
        // ITERAȚIA 4: Configurare (TVA & Nume Restaurant)
        // =============================================================
        AppConfig config = null;
        try {
            config = ConfigLoader.load("config.json");
            System.out.println("Config încarcat: restaurant = " + config.getRestaurantName() + ", TVA = " + config.getTva());
            Comanda.setTva(config.getTva()); // Setăm TVA-ul din config
        } catch (Exception e) {
            System.err.println("Eroare la incarcarea config.json: " + e.getMessage());
            // daca apare o eroare, continuam cu TVA default
            Comanda.setTva(0.09);
        }

        Meniu meniu = new Meniu();

        // =============================================================
        // POPULARE MENIU (Combinat Iteratia 1 + Iteratia 3 Pizza Builder)
        // =============================================================

        // --- Aici demonstrăm utilizarea Pizza Builder (Iterația 3) ---
        Pizza margherita = new Pizza.Builder("Pizza Margherita", 45, 450, true, "Subtire", "Rosii")
                .adaugaTopping("Mozzarella")
                .adaugaTopping("Busuioc")
                .build();

        Pizza quattro = new Pizza.Builder("Pizza Quattro Stagioni", 50, 500, false, "Pufos", "Rosii")
                .adaugaTopping("Mozzarella")
                .adaugaTopping("Ciuperci")
                .adaugaTopping("Salam")
                .build();

        meniu.adaugaProdus(Categorie.FEL_PRINCIPAL, margherita);
        meniu.adaugaProdus(Categorie.FEL_PRINCIPAL, quattro);

        // --- Restul produselor ---
        meniu.adaugaProdus(Categorie.FEL_PRINCIPAL, new Mancare("Lasagna Bolognese", 35, 350, false));
        meniu.adaugaProdus(Categorie.FEL_PRINCIPAL, new Mancare("Spaghetti Carbonara", 40, 400, false));
        meniu.adaugaProdus(Categorie.FEL_PRINCIPAL, new Mancare("Risotto cu ciuperci", 45, 450, true));
        meniu.adaugaProdus(Categorie.FEL_PRINCIPAL, new Mancare("Pasta Arrabiata", 38, 420, true));

        meniu.adaugaProdus(Categorie.BAUTURI_RACORITOARE, new Bautura("Cola", 8, 330));
        meniu.adaugaProdus(Categorie.BAUTURI_RACORITOARE, new Bautura("Fanta", 7.5, 330));
        meniu.adaugaProdus(Categorie.BAUTURI_RACORITOARE, new Bautura("Sprite", 7.5, 330));
        meniu.adaugaProdus(Categorie.BAUTURI_RACORITOARE, new Bautura("Apa minerala", 5, 500));

        meniu.adaugaProdus(Categorie.DESERT, new Mancare("Clatite cu ciocolata", 25, 200, true));
        meniu.adaugaProdus(Categorie.DESERT, new Mancare("Tiramisu", 28, 250, true));
        meniu.adaugaProdus(Categorie.DESERT, new Mancare("Cheesecake", 30, 220, true));
        meniu.adaugaProdus(Categorie.DESERT, new Mancare("Clatite cu gem", 20, 180, true));

        meniu.adaugaProdus(Categorie.BAUTURI_ALCOOLICE, new Bautura("Vin roșu", 40, 750));
        meniu.adaugaProdus(Categorie.BAUTURI_ALCOOLICE, new Bautura("Bere lager", 12, 500));
        meniu.adaugaProdus(Categorie.BAUTURI_ALCOOLICE, new Bautura("Gin", 35, 700));
        meniu.adaugaProdus(Categorie.BAUTURI_ALCOOLICE, new Bautura("Whisky", 60, 700));

        meniu.adaugaProdus(Categorie.APERITIVE, new Mancare("Bruschette cu roșii", 15, 150, true));
        meniu.adaugaProdus(Categorie.APERITIVE, new Mancare("Hummus cu pita", 18, 120, true));
        meniu.adaugaProdus(Categorie.APERITIVE, new Mancare("Aripioare de pui", 22, 180, false));
        meniu.adaugaProdus(Categorie.APERITIVE, new Mancare("Sufleu de brânză", 20, 160, true));

        // =============================================================
        // ITERAȚIA 3: DEMONSTRAȚIE INTEROGĂRI & CĂUTARE
        // =============================================================
        System.out.println("\n--- DEMONSTRAȚIE ITERAȚIA 3: Interogări Meniu ---");

        // 1. Vegetariene sortate
        System.out.println("1. Produse vegetariene (sortate alfabetic):");
        meniu.getProduseVegetarieneSortate().forEach(p -> System.out.println("   - " + p.getNume()));

        // 2. Preț mediu desert
        System.out.printf("2. Preț mediu deserturi: %.2f RON\n", meniu.getPretMediuDeserturi());

        // 3. Produs scump
        System.out.println("3. Există produse > 100 RON? " + (meniu.existaProdusePeste100() ? "DA" : "NU"));

        // 4. Căutare sigură
        System.out.print("4. Căutare 'Pizza Margherita': ");
        meniu.cautaProdus("Pizza Margherita")
                .ifPresentOrElse(
                        p -> System.out.println("GĂSIT -> " + p.detalii()),
                        () -> System.out.println("NU A FOST GĂSIT")
                );

        System.out.print("5. Căutare 'Produs Inexistent': ");
        meniu.cautaProdus("Produs Inexistent")
                .ifPresentOrElse(
                        p -> System.out.println("GĂSIT -> " + p.detalii()),
                        () -> System.out.println("NU A FOST GĂSIT (Gestionat corect cu Optional)")
                );

        // =============================================================
        // ITERAȚIA 4: EXPORT JSON
        // =============================================================
        System.out.println("\n--- DEMONSTRAȚIE ITERAȚIA 4: Export ---");
        boolean exportSuccess = meniu.exportToJson("meniu_export.json");
        if (exportSuccess) {
            System.out.println("Meniul a fost exportat cu succes în meniu_export.json");
        } else {
            System.out.println("Eroare la exportul meniului.");
        }

        // =============================================================
        // ITERAȚIA 2: DEMONSTRAȚIE COMENZI & DISCOUNT
        // =============================================================
        System.out.println("\n--- DEMONSTRAȚIE ITERAȚIA 2: Comenzi ---");

        // --- Exemplu 1: Comanda standard ---
        Comanda comanda1 = new Comanda();
        // Adaugam pizza creata cu Builder-ul
        comanda1.adaugaProdus(margherita, 2);
        comanda1.adaugaProdus(new Bautura("Cola", 8, 330), 1);

        // discount de Valentine's Day
        DiscountStrategy valentinesDay = (total, produse) -> total * 0.90;
        comanda1.setStrategieDiscount(valentinesDay);

        // bonul pentru comanda 1
        System.out.println("\n--- Bon pentru Comanda 1 (Valentine's Day -10%) ---");
        afiseazaBon(comanda1);


        // --- Exemplu 2: Comanda cu mai multe produse și discount ---
        Comanda comanda2 = new Comanda();
        comanda2.adaugaProdus(new Mancare("Spaghetti Carbonara", 40, 400, false), 3);
        comanda2.adaugaProdus(new Bautura("Fanta", 7.5, 330), 2);
        comanda2.adaugaProdus(new Mancare("Clatite cu ciocolata", 25, 200, true), 1);

        // discount fix (Weekend Special)
        DiscountStrategy weekendSpecial = (total, produse) -> total - 5;  // reducere fixă de 5 lei
        comanda2.setStrategieDiscount(weekendSpecial);

        // bonul pentru comanda 2
        System.out.println("\n--- Bon pentru Comanda 2 (Weekend Special -5 RON) ---");
        afiseazaBon(comanda2);


        // --- Exemplu 3: Comanda cu produse mai scumpe și discount fix ---
        Comanda comanda3 = new Comanda();
        comanda3.adaugaProdus(new Mancare("Risotto cu ciuperci", 45, 450, true), 2);
        comanda3.adaugaProdus(new Bautura("Vin roșu", 40, 750), 1);

        // Aplicam discountul Happy Hour (20% la bauturi)
        DiscountStrategy happyHour = (total, produse) -> {
            double reducere = 0;
            for (var entry : produse.entrySet()) {
                if (entry.getKey() instanceof Bautura) {
                    reducere += entry.getKey().getPret() * entry.getValue() * 0.20;
                }
            }
            return total - reducere;
        };
        comanda3.setStrategieDiscount(happyHour);

        // Afișăm bonul pentru comanda 3
        System.out.println("\n--- Bon pentru Comanda 3 (Happy Hour -20% la bauturi) ---");
        afiseazaBon(comanda3);
    }

    // Metoda pentru afișarea bonului cu produsele, cantitățile și prețurile
    public static void afiseazaBon(Comanda comanda) {
        System.out.println("---------------------------------------------------");
        System.out.println("Produse comandate:");

        // Iteram prin produsele din comandă și le afișam
        for (Map.Entry<Produs, Integer> entry : comanda.getProduse().entrySet()) {
            Produs produs = entry.getKey();
            int cantitate = entry.getValue();
            double pretUnitate = produs.getPret();
            double pretTotal = pretUnitate * cantitate;

            System.out.printf("%-25s %4d x %.2f RON = %.2f RON\n", produs.getNume(), cantitate, pretUnitate, pretTotal);
        }

        // totalul final
        // Atentie: calculeazaTotal returneaza pretul cu TVA. Trebuie sa extragem valoarea fara TVA corect.
        double totalCuTVA = comanda.calculeazaTotal();
        double tvaRate = Comanda.getTva();
        double totalFaraTVA = totalCuTVA / (1 + tvaRate);

        System.out.printf("\nTotal fara TVA: %.2f RON\n", totalFaraTVA);
        System.out.printf("Total cu TVA:   %.2f RON\n", totalCuTVA);
        System.out.println("---------------------------------------------------");
    }
}