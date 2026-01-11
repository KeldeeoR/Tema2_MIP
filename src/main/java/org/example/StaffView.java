package org.example;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StaffView {

    private final Stage stage;
    private final User ospatar;
    private final Meniu meniu;
    private final ComandaRepository repo;
    private Comanda comandaCurenta = null;

    private ListView<ComandaItem> cartListView;
    private Label totalLabel;
    private Label offersLabel;
    private Label detailName, detailPrice, detailExtra;

    public StaffView(Stage stage, User ospatar, Meniu meniu) {
        this.stage = stage;
        this.ospatar = ospatar;
        this.meniu = meniu;
        this.repo = new ComandaRepository();
    }

    public void showTableSelection() {
        TabPane tabPane = new TabPane();
        Tab tablesTab = new Tab("Sala de Mese", createTablesContent());
        tablesTab.setClosable(false);
        Tab historyTab = new Tab("Istoricul Meu", createMyHistoryContent());
        historyTab.setClosable(false);

        historyTab.setOnSelectionChanged(e -> {
            if (historyTab.isSelected()) historyTab.setContent(createMyHistoryContent());
        });

        tabPane.getTabs().addAll(tablesTab, historyTab);

        VBox root = new VBox(tabPane);
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        HBox topBar = new HBox(10);
        topBar.setPadding(new Insets(10));
        topBar.setAlignment(Pos.CENTER_RIGHT);
        topBar.setStyle("-fx-background-color: #eee;");
        topBar.getChildren().addAll(new Label("Logat ca: " + ospatar.getUsername()), createLogoutButton());
        root.getChildren().add(0, topBar);

        stage.setScene(new Scene(root, 950, 650));
        stage.setTitle("Staff Dashboard - " + ospatar.getUsername());
    }

    private Button createLogoutButton() {
        Button btn = new Button("Deconectare");
        btn.setStyle("-fx-background-color: #F44336; -fx-text-fill: white;");
        btn.setOnAction(e -> {
            try { new MainApp().start(stage); } catch (Exception ex) { ex.printStackTrace(); }
        });
        return btn;
    }

    private VBox createTablesContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setAlignment(Pos.CENTER);
        FlowPane tablesPanel = new FlowPane();
        tablesPanel.setHgap(20); tablesPanel.setVgap(20); tablesPanel.setAlignment(Pos.CENTER);

        for (Masa masa : repo.getToateMesele()) {
            Button tableBtn = new Button("Masa " + masa.getNumarMasa());
            tableBtn.setPrefSize(120, 100);
            tableBtn.setStyle(masa.isEsteOcupata() ? "-fx-background-color: #FFCDD2; -fx-border-color: red;" : "-fx-background-color: #C8E6C9; -fx-border-color: green;");
            tableBtn.setOnAction(e -> openOrderScreen(masa));
            tablesPanel.getChildren().add(tableBtn);
        }
        content.getChildren().addAll(new Label("Selectează Masa:"), tablesPanel);
        return content;
    }

    // --- REPARATIE ISTORIC: GRUPARE VIZUALĂ (AGREGARE) ---
    private VBox createMyHistoryContent() {
        VBox content = new VBox(10); content.setPadding(new Insets(15));
        ListView<String> myHistoryList = new ListView<>();
        List<Comanda> myOrders = repo.getToateComenzile().stream()
                .filter(c -> c.getOspatar() != null && c.getOspatar().getUsername().equals(ospatar.getUsername()) && c.isFinalizata())
                .collect(Collectors.toList());

        if (myOrders.isEmpty()) {
            myHistoryList.getItems().add("Nu ai comenzi finalizate.");
        } else {
            for (Comanda c : myOrders) {
                StringBuilder sb = new StringBuilder();
                sb.append(String.format("=== [Masa %d]  %s ===\n", c.getMasa().getNumarMasa(), c.getDataOra().toString().substring(0, 16)));

                // 1. Agregăm produsele (Grupăm după nume)
                Map<String, Integer> counts = new HashMap<>();
                Map<String, Double> unitPrices = new HashMap<>();

                for (ComandaItem item : c.getItems()) {
                    String name = item.getProdus().getNume();
                    counts.put(name, counts.getOrDefault(name, 0) + item.getCantitate());
                    unitPrices.put(name, item.getProdus().getPret());
                }

                // 2. Afișăm lista agregată
                for (String name : counts.keySet()) {
                    int qty = counts.get(name);
                    double price = unitPrices.get(name);
                    double totalLine = qty * price;
                    sb.append(String.format("   %-20s x %d  (%.2f RON/buc) = %.2f RON\n", name, qty, price, totalLine));
                }

                // 3. Calculăm și afișăm ofertele
                double subtotal = c.calculeazaTotal();
                double reducere = subtotal - c.getTotalPlata();

                if (reducere > 0.01) {
                    sb.append(String.format("\n   Subtotal: %.2f RON\n", subtotal));
                    sb.append(String.format("   REDUCERI: -%.2f RON\n", reducere));

                    // Afișăm numele ofertelor aplicabile
                    for(OfferStrategy s : OfferManager.getInstance().getActiveOffers()) {
                        if(s.calculeazaReducere(c) > 0) {
                            sb.append("     * " + s.getNumeOferta() + "\n");
                        }
                    }
                }

                sb.append(String.format("TOTAL FINAL: %.2f RON\n", c.getTotalPlata()));
                myHistoryList.getItems().add(sb.toString());
            }
        }
        content.getChildren().addAll(new Label("Istoricul meu (Agregat):"), myHistoryList);
        return content;
    }

    private void openOrderScreen(Masa masa) {
        comandaCurenta = repo.gasesteComandaActiva(masa);
        if (comandaCurenta == null) {
            comandaCurenta = new Comanda(); comandaCurenta.setMasa(masa); comandaCurenta.setOspatar(ospatar);
            masa.setEsteOcupata(true); repo.actualizeazaMasa(masa);
        }

        BorderPane root = new BorderPane();
        HBox topBar = new HBox(20); topBar.setPadding(new Insets(10)); topBar.setStyle("-fx-background-color: #ddd;");
        Button backBtn = new Button("<- Înapoi"); backBtn.setOnAction(e -> showTableSelection());
        topBar.getChildren().addAll(backBtn, new Label("Comandă Masa " + masa.getNumarMasa()));
        root.setTop(topBar);

        ListView<Produs> menuList = new ListView<>();
        meniu.produsePeCategorii.values().forEach(list -> menuList.getItems().addAll(list));
        menuList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> updateDetailsPanel(newVal));
        menuList.setOnMouseClicked(e -> { if (e.getClickCount() == 2 && menuList.getSelectionModel().getSelectedItem() != null) adaugaInCos(menuList.getSelectionModel().getSelectedItem()); });
        root.setLeft(new VBox(new Label("Meniu:"), menuList));

        SplitPane splitPane = new SplitPane(); splitPane.setOrientation(Orientation.VERTICAL);
        VBox detailsPane = new VBox(10); detailsPane.setPadding(new Insets(10)); detailsPane.setStyle("-fx-background-color: #f0f8ff;");
        detailsPane.getChildren().add(new Label("Detalii Produs Selectat:"));
        detailName = new Label("-"); detailName.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        detailPrice = new Label("-"); detailExtra = new Label("-");
        detailsPane.getChildren().addAll(detailName, detailPrice, detailExtra);

        cartListView = new ListView<>();
        cartListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(ComandaItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setGraphic(null); } else {
                    HBox box = new HBox(10); box.setAlignment(Pos.CENTER_LEFT);
                    Label nameLbl = new Label(item.getProdus().getNume());
                    Label qtyLbl = new Label("x" + item.getCantitate());
                    Label priceLbl = new Label(String.format("%.2f", item.getSubtotal()));
                    Button minusBtn = new Button("-"); minusBtn.setOnAction(e -> modificaCantitate(item, -1));
                    Button plusBtn = new Button("+"); plusBtn.setOnAction(e -> modificaCantitate(item, 1));
                    Button delBtn = new Button("X"); delBtn.setStyle("-fx-text-fill: red;"); delBtn.setOnAction(e -> stergeLinie(item));
                    Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
                    box.getChildren().addAll(nameLbl, spacer, minusBtn, qtyLbl, plusBtn, priceLbl, delBtn);
                    setGraphic(box);
                }
            }
        });

        totalLabel = new Label("Total: 0.00 RON"); totalLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        offersLabel = new Label(""); offersLabel.setStyle("-fx-text-fill: #2E7D32; -fx-font-weight: bold;");
        Button payBtn = new Button("Încasează"); payBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        payBtn.setOnAction(e -> incaseazaComanda(masa));

        VBox cartPane = new VBox(5); cartPane.setPadding(new Insets(10));
        cartPane.getChildren().addAll(new Label("Coșul de cumpărături:"), cartListView, offersLabel, new Separator(), totalLabel, payBtn);
        splitPane.getItems().addAll(detailsPane, cartPane); splitPane.setDividerPositions(0.3);
        root.setCenter(splitPane);

        refreshCartView();
        stage.setScene(new Scene(root, 950, 650));
    }

    private void updateDetailsPanel(Produs p) { if (p == null) return; detailName.setText(p.getNume()); detailPrice.setText(p.getPret() + " RON"); detailExtra.setText(p.detalii()); }
    private void adaugaInCos(Produs p) { comandaCurenta.adaugaProdus(p, 1); refreshCartView(); }
    private void modificaCantitate(ComandaItem item, int delta) { int newQty = item.getCantitate() + delta; if (newQty > 0) item.setCantitate(newQty); else comandaCurenta.getItems().remove(item); refreshCartView(); }
    private void stergeLinie(ComandaItem item) { comandaCurenta.getItems().remove(item); refreshCartView(); }

    private void refreshCartView() {
        cartListView.getItems().clear();
        cartListView.getItems().addAll(comandaCurenta.getItems());
        double subtotal = comandaCurenta.calculeazaTotal();
        double totalReducere = OfferManager.getInstance().aplicaOferte(comandaCurenta);
        double total = subtotal - totalReducere; if (total < 0) total = 0;

        StringBuilder offersText = new StringBuilder();
        if (totalReducere > 0.01) {
            offersText.append("Oferte APLICATE: ");
            for (OfferStrategy s : OfferManager.getInstance().getActiveOffers()) {
                if(s.calculeazaReducere(comandaCurenta) > 0.01) {
                    offersText.append(s.getNumeOferta()).append(", ");
                }
            }
            // Scoatem ultima virgula
            if(offersText.toString().endsWith(", ")) {
                offersText.setLength(offersText.length() - 2);
            }
        } else {
            offersText.append("");
        }
        offersLabel.setText(offersText.toString());
        totalLabel.setText(String.format("Subtotal: %.2f | Reduceri: -%.2f | FINAL: %.2f RON", subtotal, totalReducere, total));
        repo.salveazaComanda(comandaCurenta);
    }

    private void incaseazaComanda(Masa masa) {
        refreshCartView();
        double subtotal = comandaCurenta.calculeazaTotal();
        double reduceri = OfferManager.getInstance().aplicaOferte(comandaCurenta);
        comandaCurenta.setTotalPlata(Math.max(0, subtotal - reduceri));
        comandaCurenta.setFinalizata(true);
        repo.salveazaComanda(comandaCurenta);
        masa.setEsteOcupata(false);
        repo.actualizeazaMasa(masa);
        new Alert(Alert.AlertType.INFORMATION, "Comandă Încasată!").showAndWait();
        showTableSelection();
    }
}