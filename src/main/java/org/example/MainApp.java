package org.example;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.util.List;

public class MainApp extends Application {

    private ListView<Produs> productListView;
    private TextField nameField;
    private TextField priceField;
    private Label typeLabel;
    private Label extraLabel;

    private Meniu meniu;
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        meniu = new Meniu();
        seedData();
        LoginView loginView = new LoginView(primaryStage, this);
        loginView.show();
    }

    public void openMainInterface(User user) {
        Role role = user.getRole();

        if (role == Role.STAFF) {
            new StaffView(primaryStage, user, meniu).showTableSelection();
            return;
        }

        if (role == Role.ADMIN) {
            new AdminView(primaryStage, this).show();
            return;
        }

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        HBox topContainer = new HBox(10);
        topContainer.setAlignment(Pos.CENTER_LEFT);
        topContainer.setPadding(new Insets(0, 0, 10, 0));
        Label userLabel = new Label("GUEST MODE (Client)");
        userLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button exitBtn = new Button("Înapoi la Login");
        exitBtn.setStyle("-fx-background-color: #F44336; -fx-text-fill: white;");
        exitBtn.setOnAction(e -> {
            LoginView loginView = new LoginView(primaryStage, this);
            loginView.show();
        });
        topContainer.getChildren().addAll(userLabel, spacer, exitBtn);
        root.setTop(topContainer);

        productListView = new ListView<>();
        refreshProductList();

        VBox leftPane = new VBox(5);
        if (role == null) {
            TitledPane filterPane = new TitledPane();
            filterPane.setText("Filtrare & Căutare");
            filterPane.setCollapsible(false);
            GridPane filterGrid = new GridPane();
            filterGrid.setHgap(5); filterGrid.setVgap(5); filterGrid.setPadding(new Insets(5));
            TextField searchField = new TextField(); searchField.setPromptText("Caută produs...");
            TextField minPrice = new TextField(); minPrice.setPromptText("Min"); minPrice.setPrefWidth(50);
            TextField maxPrice = new TextField(); maxPrice.setPromptText("Max"); maxPrice.setPrefWidth(50);
            ComboBox<String> typeCombo = new ComboBox<>(); typeCombo.getItems().addAll("Toate", "Mancare", "Bautura"); typeCombo.setValue("Toate");
            CheckBox vegCheck = new CheckBox("Doar Vegetarian");
            Button filterButton = new Button("Aplică Filtre");
            filterButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
            filterButton.setOnAction(e -> {
                String cautare = searchField.getText();
                String tip = typeCombo.getValue().equals("Toate") ? null : typeCombo.getValue();
                boolean veg = vegCheck.isSelected();
                Double min = null, max = null;
                try { if (!minPrice.getText().isEmpty()) min = Double.parseDouble(minPrice.getText()); } catch (Exception ex) {}
                try { if (!maxPrice.getText().isEmpty()) max = Double.parseDouble(maxPrice.getText()); } catch (Exception ex) {}
                List<Produs> rezultate = meniu.filtreazaProduse(cautare, min, max, tip, veg);
                productListView.getItems().clear();
                productListView.getItems().addAll(rezultate);
            });
            filterGrid.add(new Label("Nume:"), 0, 0); filterGrid.add(searchField, 1, 0, 2, 1);
            filterGrid.add(new Label("Preț:"), 0, 1); filterGrid.add(minPrice, 1, 1); filterGrid.add(maxPrice, 2, 1);
            filterGrid.add(new Label("Tip:"), 0, 2);  filterGrid.add(typeCombo, 1, 2, 2, 1);
            filterGrid.add(vegCheck, 0, 3, 3, 1);
            filterGrid.add(filterButton, 0, 4, 3, 1);
            filterPane.setContent(filterGrid);
            leftPane.getChildren().add(filterPane);
        }
        leftPane.getChildren().add(new Label("Rezultate:"));
        leftPane.getChildren().add(productListView);
        root.setLeft(leftPane);

        GridPane formPane = new GridPane();
        formPane.setHgap(10); formPane.setVgap(10); formPane.setPadding(new Insets(20));
        formPane.setAlignment(Pos.CENTER);
        Label l1 = new Label("Nume:"); nameField = new TextField(); nameField.setEditable(false);
        Label l2 = new Label("Preț:"); priceField = new TextField();
        Label l3 = new Label("Tip:"); typeLabel = new Label("-");
        Label l4 = new Label("Extra:"); extraLabel = new Label("-");
        productListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showProductDetails(newValue)
        );
        if (role == null) { priceField.setEditable(false); priceField.setStyle("-fx-background-color: #f4f4f4;"); }
        formPane.add(l1, 0, 0); formPane.add(nameField, 1, 0);
        formPane.add(l2, 0, 1); formPane.add(priceField, 1, 1);
        formPane.add(l3, 0, 2); formPane.add(typeLabel, 1, 2);
        formPane.add(l4, 0, 3); formPane.add(extraLabel, 1, 3);
        VBox centerPane = new VBox(new Label("Detalii Produs"), formPane);
        centerPane.setAlignment(Pos.TOP_CENTER);
        root.setCenter(centerPane);

        Scene scene = new Scene(root, 950, 600);
        primaryStage.setTitle("Restaurant App - Guest Mode");
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    private void showProductDetails(Produs produs) {
        if (produs != null) {
            nameField.setText(produs.getNume());
            priceField.setText(String.valueOf(produs.getPret()));
            if (produs instanceof Mancare) {
                typeLabel.setText("Mancare" + (((Mancare) produs).isVegetarian() ? " (Veg)" : ""));
                extraLabel.setText(((Mancare) produs).getGramaj() + "g");
            } else if (produs instanceof Bautura) {
                typeLabel.setText("Bautura");
                extraLabel.setText(((Bautura) produs).getVolum() + "ml");
            }
        } else {
            nameField.setText(""); priceField.setText(""); typeLabel.setText(""); extraLabel.setText("");
        }
    }

    private void refreshProductList() {
        productListView.getItems().clear();
        for (var entry : meniu.produsePeCategorii.entrySet()) {
            productListView.getItems().addAll(entry.getValue());
        }
    }

    private void seedData() {
        if(OfferManager.getInstance().getActiveOffers().isEmpty()) {
            OfferManager.getInstance().enableOffer(new HappyHourStrategy());
            OfferManager.getInstance().enableOffer(new MealDealStrategy());
            OfferManager.getInstance().enableOffer(new PartyPackStrategy());
        }

        UserRepository userRepo = new UserRepository();
        if (userRepo.login("admin", "admin").isEmpty()) {
            userRepo.adaugaUser(new User("admin", "admin", Role.ADMIN));
            userRepo.adaugaUser(new User("ospatar", "1234", Role.STAFF));
        }
        ComandaRepository cmdRepo = new ComandaRepository();
        if (cmdRepo.getToateMesele().isEmpty()) {
            for (int i = 1; i <= 6; i++) cmdRepo.salveazaMasa(new Masa(i));
        }
        ProdusRepository repo = new ProdusRepository();
        List<Produs> produseExistente = repo.incarcaToateProdusele();
        if (produseExistente.isEmpty()) {
            Pizza margherita = new Pizza.Builder("Pizza Margherita", 45, 450, true, "Subtire", "Rosii").adaugaTopping("Mozzarella").build();
            Mancare paste = new Mancare("Spaghetti Carbonara", 40, 400, false);
            Mancare tiramisu = new Mancare("Tiramisu", 28, 200, true);
            Bautura cola = new Bautura("Cola", 8, 330);
            repo.adaugaProdus(margherita); repo.adaugaProdus(paste); repo.adaugaProdus(tiramisu); repo.adaugaProdus(cola);
            produseExistente = repo.incarcaToateProdusele();
        }
        for (Produs p : produseExistente) meniu.adaugaProdus(Categorie.FEL_PRINCIPAL, p);
    }

    public static void main(String[] args) { launch(args); }
}