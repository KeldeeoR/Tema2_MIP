package org.example;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

// Aceasta este clasa principala pentru JavaFX (Iterația 6 Finală - cu Meniu Import/Export)
public class MainApp extends Application {

    // Componente UI pe care trebuie să le accesăm din diverse metode
    private ListView<Produs> productListView;
    private TextField nameField;
    private TextField priceField;
    private Label typeLabel; // Afișează dacă e Mâncare sau Băutură
    private Label extraLabel; // Afișează Gramaj sau Volum

    // Referință către date
    private Meniu meniu;

    @Override
    public void start(Stage primaryStage) {
        // 1. Inițializare date
        meniu = new Meniu();
        seedData(); // Populăm cu date din DB

        // 2. Configurare Layout Principal
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        // =============================================================
        //  NOU: BARA DE MENIU (Iterația 6)
        // =============================================================
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");

        MenuItem exportItem = new MenuItem("Export JSON...");
        exportItem.setOnAction(e -> handleExport(primaryStage));

        MenuItem importItem = new MenuItem("Import JSON...");
        importItem.setOnAction(e -> handleImport(primaryStage));

        fileMenu.getItems().addAll(exportItem, importItem);
        menuBar.getMenus().add(fileMenu);

        // Punem meniul sus de tot
        VBox topContainer = new VBox(menuBar);
        topContainer.setSpacing(10);
        root.setTop(topContainer);
        // =============================================================

        // --- PARTEA STÂNGĂ (Lista) ---
        productListView = new ListView<>();
        // Convertim lista din map-ul meniului într-o listă simplă pentru afișare
        refreshProductList();

        // Listener pentru selecție (Reactivitate)
        productListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showProductDetails(newValue)
        );

        VBox leftPane = new VBox(new Label("Lista Produse:"), productListView);
        leftPane.setSpacing(5);
        root.setLeft(leftPane);


        // --- CENTRU (Formular Detalii) ---
        GridPane formPane = new GridPane();
        formPane.setHgap(10);
        formPane.setVgap(10);
        formPane.setPadding(new Insets(20));
        formPane.setAlignment(Pos.CENTER);

        // Creăm câmpurile formularului
        Label l1 = new Label("Nume Produs:");
        nameField = new TextField();
        nameField.setEditable(false);

        Label l2 = new Label("Preț (RON):");
        priceField = new TextField();

        // Listener pe preț
        priceField.textProperty().addListener((obs, oldText, newText) -> {
            Produs selected = productListView.getSelectionModel().getSelectedItem();
            if (selected != null && newText.matches("\\d+(\\.\\d+)?")) {
                selected.pret = Double.parseDouble(newText);
            }
        });

        Label l3 = new Label("Tip:");
        typeLabel = new Label("-");

        Label l4 = new Label("Detalii Extra:");
        extraLabel = new Label("-");

        // Adăugăm elementele în grilă
        formPane.add(l1, 0, 0); formPane.add(nameField, 1, 0);
        formPane.add(l2, 0, 1); formPane.add(priceField, 1, 1);
        formPane.add(l3, 0, 2); formPane.add(typeLabel, 1, 2);
        formPane.add(l4, 0, 3); formPane.add(extraLabel, 1, 3);

        // Grupăm formularul într-un VBox
        VBox centerPane = new VBox(new Label("Detalii Produs Selectat"), formPane);
        centerPane.setAlignment(Pos.TOP_CENTER);
        centerPane.setPadding(new Insets(0, 0, 0, 20));

        root.setCenter(centerPane);

        // 3. Configurare Scenă și Fereastră
        Scene scene = new Scene(root, 800, 550); // Marit putin pentru a incapea meniul
        primaryStage.setTitle("Restaurant Management System - Iterația 6 (DB + JSON)");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // --- Metode pentru butoanele de meniu (Import/Export) ---

    private void handleExport(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salvează Meniu JSON");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        fileChooser.setInitialFileName("meniu_backup.json");

        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            boolean success = meniu.exportToJson(file.getAbsolutePath());
            if (success) {
                new Alert(Alert.AlertType.INFORMATION, "Meniu exportat cu succes!").show();
            } else {
                new Alert(Alert.AlertType.ERROR, "Eroare la export.").show();
            }
        }
    }

    private void handleImport(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Încarcă Meniu JSON");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));

        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            // 1. Citim produsele din fișier (logica e in Meniu.java)
            List<Produs> produseNoi = meniu.importFromJson(file.getAbsolutePath());

            if (!produseNoi.isEmpty()) {
                // 2. Le salvăm în Baza de Date
                ProdusRepository repo = new ProdusRepository();
                int contor = 0;
                for (Produs p : produseNoi) {
                    repo.adaugaProdus(p);
                    // Le adăugăm și în memorie (Meniu) ca să apară în listă imediat
                    meniu.adaugaProdus(Categorie.FEL_PRINCIPAL, p);
                    contor++;
                }

                // 3. Facem refresh la listă
                refreshProductList();
                new Alert(Alert.AlertType.INFORMATION, "S-au importat " + contor + " produse în Baza de Date!").show();
            } else {
                new Alert(Alert.AlertType.WARNING, "Fișierul pare gol sau invalid.").show();
            }
        }
    }

    // Metodă ajutătoare pentru a popula formularul din dreapta
    private void showProductDetails(Produs produs) {
        if (produs != null) {
            nameField.setText(produs.getNume());
            priceField.setText(String.valueOf(produs.getPret()));

            if (produs instanceof Mancare) {
                typeLabel.setText("Mâncare" + (((Mancare) produs).isVegetarian() ? " (Veg)" : ""));
                extraLabel.setText("Gramaj: " + ((Mancare) produs).getGramaj() + "g");
            } else if (produs instanceof Bautura) {
                typeLabel.setText("Băutură");
                extraLabel.setText("Volum: " + ((Bautura) produs).getVolum() + "ml");
            } else {
                typeLabel.setText("Necunoscut");
                extraLabel.setText("-");
            }
        } else {
            // Dacă nu e nimic selectat, golim câmpurile
            nameField.setText("");
            priceField.setText("");
            typeLabel.setText("");
            extraLabel.setText("");
        }
    }

    // Metodă pentru a adăuga date de test
    private void refreshProductList() {
        productListView.getItems().clear();
        for (var entry : meniu.produsePeCategorii.entrySet()) {
            productListView.getItems().addAll(entry.getValue());
        }
    }

    private void seedData() {
        ProdusRepository repo = new ProdusRepository();

        // 1. Încercăm să încărcăm produsele din baza de date
        List<Produs> produseExistente = repo.incarcaToateProdusele();

        // 2. Dacă baza e goală (prima rulare), o populăm noi
        if (produseExistente.isEmpty()) {
            System.out.println("Baza de date e goală. Se populează...");

            repo.adaugaProdus(new Mancare("Pizza Margherita", 45, 450, true));
            repo.adaugaProdus(new Mancare("Spaghetti Carbonara", 40, 400, false));
            repo.adaugaProdus(new Bautura("Cola", 8, 330));
            repo.adaugaProdus(new Mancare("Tiramisu", 28, 250, true));

            // Le reîncărcăm ca să fim siguri că avem datele salvate
            produseExistente = repo.incarcaToateProdusele();
        } else {
            System.out.println("S-au încărcat " + produseExistente.size() + " produse din baza de date.");
        }

        // 3. Adăugăm produsele încărcate în meniul aplicației pentru afișare
        for (Produs p : produseExistente) {
            meniu.adaugaProdus(Categorie.FEL_PRINCIPAL, p);
        }
    }

    // Metoda main pentru lansare
    public static void main(String[] args) {
        launch(args);
    }
}