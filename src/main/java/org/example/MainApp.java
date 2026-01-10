package org.example;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

// Aceasta este clasa principala pentru JavaFX (Iterația 5)
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
        // 1. Inițializare date (folosim ce ai scris la temele trecute)
        meniu = new Meniu();
        seedData(); // Populăm cu date de test

        // 2. Configurare Layout Principal (BorderPane conform cerinței)
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        // --- PARTEA STÂNGĂ (Lista) ---
        productListView = new ListView<>();
        // Convertim lista din map-ul meniului într-o listă simplă pentru afișare
        refreshProductList();

        // Listener pentru selecție (Reactivitate): Când userul alege un produs, actualizăm formularul
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
        nameField.setEditable(false); // Numele e doar de citit momentan

        Label l2 = new Label("Preț (RON):");
        priceField = new TextField();

        // Listener pe preț (Reactivitate bidirecțională simplificată)
        // Dacă modific prețul în GUI, se modifică și în obiectul din memorie
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

        // Adăugăm elementele în grilă (Coloana, Rândul)
        formPane.add(l1, 0, 0); formPane.add(nameField, 1, 0);
        formPane.add(l2, 0, 1); formPane.add(priceField, 1, 1);
        formPane.add(l3, 0, 2); formPane.add(typeLabel, 1, 2);
        formPane.add(l4, 0, 3); formPane.add(extraLabel, 1, 3);

        // Grupăm formularul într-un VBox cu titlu
        VBox centerPane = new VBox(new Label("Detalii Produs Selectat"), formPane);
        centerPane.setAlignment(Pos.TOP_CENTER);
        centerPane.setPadding(new Insets(0, 0, 0, 20)); // Puțin spațiu la stânga

        root.setCenter(centerPane);

        // 3. Configurare Scenă și Fereastră
        Scene scene = new Scene(root, 800, 500);
        primaryStage.setTitle("Restaurant Management System - Iterația 5");
        primaryStage.setScene(scene);
        primaryStage.show();
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

    // Metodă pentru a adăuga date de test (copiate din Main-ul vechi)
    private void refreshProductList() {
        productListView.getItems().clear();
        // Extragem toate produsele din toate categoriile într-o singură listă pentru GUI
        // Notă: Asta e o soluție temporară, ideal am avea un filtru pe categorii
        for (var entry : meniu.produsePeCategorii.entrySet()) {
            productListView.getItems().addAll(entry.getValue());
        }
    }

    private void seedData() {
        // Aceleași date ca în Tema 1
        meniu.adaugaProdus(Categorie.FEL_PRINCIPAL, new Mancare("Pizza Margherita", 45, 450, true));
        meniu.adaugaProdus(Categorie.FEL_PRINCIPAL, new Mancare("Spaghetti Carbonara", 40, 400, false));
        meniu.adaugaProdus(Categorie.BAUTURI_RACORITOARE, new Bautura("Cola", 8, 330));
        meniu.adaugaProdus(Categorie.DESERT, new Mancare("Tiramisu", 28, 250, true));
    }

    // Metoda main pentru lansare
    public static void main(String[] args) {
        launch(args);
    }
}