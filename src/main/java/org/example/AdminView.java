package org.example;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminView {

    private final Stage stage;
    private final MainApp mainApp;
    private final UserRepository userRepo;
    private final ProdusRepository produsRepo;
    private final ComandaRepository cmdRepo;
    private ListView<String> historyList;

    public AdminView(Stage stage, MainApp mainApp) {
        this.stage = stage;
        this.mainApp = mainApp;
        this.userRepo = new UserRepository();
        this.produsRepo = new ProdusRepository();
        this.cmdRepo = new ComandaRepository();
    }

    public void show() {
        TabPane tabPane = new TabPane();
        Tab staffTab = new Tab("Angajați", createStaffPane()); staffTab.setClosable(false);
        Tab menuTab = new Tab("Gestionare Meniu", createMenuPane()); menuTab.setClosable(false);
        Tab offersTab = new Tab("Control Oferte", createOffersPane()); offersTab.setClosable(false);
        Tab historyTab = new Tab("Istoric Comenzi", createHistoryPane()); historyTab.setClosable(false);

        historyTab.setOnSelectionChanged(e -> {
            if (historyTab.isSelected()) refreshHistoryList();
        });

        tabPane.getTabs().addAll(staffTab, menuTab, offersTab, historyTab);
        VBox root = new VBox(tabPane);
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        Button logoutBtn = new Button("Deconectare & Ieșire");
        logoutBtn.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-font-weight: bold;");
        logoutBtn.setMaxWidth(Double.MAX_VALUE);
        logoutBtn.setOnAction(e -> { try { new MainApp().start(stage); } catch (Exception ex) { ex.printStackTrace(); } });
        root.getChildren().add(logoutBtn);

        Scene scene = new Scene(root, 950, 650);
        stage.setScene(scene);
        stage.setTitle("Admin Panel - Manager Restaurant");
    }

    private Pane createStaffPane() {
        BorderPane pane = new BorderPane(); pane.setPadding(new Insets(15));
        ListView<String> staffList = new ListView<>(); refreshStaffList(staffList);
        VBox form = new VBox(10); form.setPadding(new Insets(10));
        TextField userField = new TextField(); userField.setPromptText("Username");
        PasswordField passField = new PasswordField(); passField.setPromptText("Parola");
        Button addBtn = new Button("Adaugă Ospătar");
        addBtn.setOnAction(e -> {
            if (!userField.getText().isEmpty() && !passField.getText().isEmpty()) {
                userRepo.adaugaUser(new User(userField.getText(), passField.getText(), Role.STAFF));
                refreshStaffList(staffList); userField.clear(); passField.clear();
                new Alert(Alert.AlertType.INFORMATION, "Angajat salvat!").show();
            }
        });
        Button deleteBtn = new Button("Concediază Selectat");
        deleteBtn.setStyle("-fx-background-color: #F44336; -fx-text-fill: white;");
        deleteBtn.setOnAction(e -> {
            String selected = staffList.getSelectionModel().getSelectedItem();
            if (selected != null && !selected.startsWith("admin")) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Ești sigur? Se vor șterge și comenzile lui!", ButtonType.YES, ButtonType.NO);
                alert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.YES) {
                        userRepo.stergeUser(selected.split(" \\(")[0]);
                        refreshStaffList(staffList); refreshHistoryList();
                    }
                });
            }
        });
        form.getChildren().addAll(new Label("Angajează:"), userField, passField, addBtn, new Separator(), deleteBtn);
        pane.setCenter(staffList); pane.setRight(form);
        return pane;
    }

    private void refreshStaffList(ListView<String> list) {
        list.getItems().clear();
        for (User u : userRepo.getAllUsers()) list.getItems().add(u.getUsername() + " (" + u.getRole() + ")");
    }

    private Pane createMenuPane() {
        BorderPane pane = new BorderPane(); pane.setPadding(new Insets(15));
        ListView<Produs> prodList = new ListView<>(); refreshProdList(prodList);
        VBox form = new VBox(10); form.setPadding(new Insets(10)); form.setPrefWidth(300);

        TextField idField = new TextField(); idField.setVisible(false); idField.setManaged(false);
        TextField nameField = new TextField(); nameField.setPromptText("Nume");
        TextField priceField = new TextField(); priceField.setPromptText("Preț");
        ComboBox<String> typeCombo = new ComboBox<>(); typeCombo.getItems().addAll("Mancare", "Bautura"); typeCombo.setValue("Mancare");
        CheckBox vegCheck = new CheckBox("Vegetarian?");
        typeCombo.setOnAction(e -> { if (typeCombo.getValue().equals("Bautura")) { vegCheck.setSelected(false); vegCheck.setDisable(true); } else { vegCheck.setDisable(false); } });

        Button saveBtn = new Button("Salvează Produs");
        saveBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        saveBtn.setOnAction(e -> {
            try {
                String n = nameField.getText(); double p = Double.parseDouble(priceField.getText());
                Produs prod = typeCombo.getValue().equals("Mancare") ? new Mancare(n, p, 300, vegCheck.isSelected()) : new Bautura(n, p, 250);
                if (!idField.getText().isEmpty()) { prod.id = Long.parseLong(idField.getText()); produsRepo.actualizeazaProdus(prod); }
                else produsRepo.adaugaProdus(prod);
                refreshProdList(prodList); nameField.clear(); priceField.clear(); idField.setText("");
            } catch(Exception ex) { new Alert(Alert.AlertType.ERROR, "Date invalide").show(); }
        });

        Button editBtn = new Button("Editează");
        editBtn.setOnAction(e -> {
            Produs p = prodList.getSelectionModel().getSelectedItem();
            if(p!=null) { idField.setText(p.getId().toString()); nameField.setText(p.getNume()); priceField.setText(""+p.getPret()); }
        });

        Button deleteBtn = new Button("Șterge");
        deleteBtn.setStyle("-fx-background-color: #F44336; -fx-text-fill: white;");
        deleteBtn.setOnAction(e -> {
            Produs p = prodList.getSelectionModel().getSelectedItem();
            if(p!=null) { produsRepo.stergeProdus(p); refreshProdList(prodList); }
        });

        Button exportBtn = new Button("Export JSON");
        exportBtn.setOnAction(e -> {
            FileChooser fc = new FileChooser(); fc.setInitialFileName("meniu.json");
            File f = fc.showSaveDialog(stage);
            if(f!=null) {
                Meniu m = new Meniu();
                produsRepo.incarcaToateProdusele().forEach(p -> m.adaugaProdus(Categorie.FEL_PRINCIPAL, p));
                m.exportToJson(f.getAbsolutePath());
                new Alert(Alert.AlertType.INFORMATION, "Export Reușit!").show();
            }
        });

        Button importBtn = new Button("Import JSON");
        importBtn.setOnAction(e -> {
            FileChooser fc = new FileChooser(); File f = fc.showOpenDialog(stage);
            if(f!=null) {
                new Meniu().importFromJson(f.getAbsolutePath()).forEach(produsRepo::adaugaProdus);
                refreshProdList(prodList);
                new Alert(Alert.AlertType.INFORMATION, "Import Reușit!").show();
            }
        });

        form.getChildren().addAll(new Label("Produs:"), nameField, priceField, typeCombo, vegCheck, saveBtn, editBtn, deleteBtn, new Separator(), new Label("Backup:"), exportBtn, importBtn);
        pane.setCenter(prodList); pane.setRight(form);
        return pane;
    }

    private void refreshProdList(ListView<Produs> list) { list.getItems().clear(); list.getItems().addAll(produsRepo.incarcaToateProdusele()); }

    private Pane createOffersPane() {
        VBox root = new VBox(20); root.setPadding(new Insets(30)); root.setAlignment(Pos.TOP_CENTER);
        Label title = new Label("Activează / Dezactivează Promoții"); title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        CheckBox chkHappy = new CheckBox("Happy Hour (50% la a 2-a băutură)");
        chkHappy.setSelected(isOfferActive(HappyHourStrategy.class));
        chkHappy.setOnAction(e -> toggleOffer(chkHappy.isSelected(), new HappyHourStrategy(), HappyHourStrategy.class));
        CheckBox chkMeal = new CheckBox("Meal Deal (-25% Desert la Pizza)");
        chkMeal.setSelected(isOfferActive(MealDealStrategy.class));
        chkMeal.setOnAction(e -> toggleOffer(chkMeal.isSelected(), new MealDealStrategy(), MealDealStrategy.class));
        CheckBox chkParty = new CheckBox("Party Pack (1 Pizza Gratis la 4)");
        chkParty.setSelected(isOfferActive(PartyPackStrategy.class));
        chkParty.setOnAction(e -> toggleOffer(chkParty.isSelected(), new PartyPackStrategy(), PartyPackStrategy.class));
        root.getChildren().addAll(title, chkHappy, chkMeal, chkParty);
        return root;
    }
    private boolean isOfferActive(Class<? extends OfferStrategy> clazz) { return OfferManager.getInstance().getActiveOffers().stream().anyMatch(o -> o.getClass().equals(clazz)); }
    private void toggleOffer(boolean enable, OfferStrategy s, Class<? extends OfferStrategy> c) { if (enable) OfferManager.getInstance().enableOffer(s); else OfferManager.getInstance().disableOffer(c); }

    private Pane createHistoryPane() {
        BorderPane pane = new BorderPane(); pane.setPadding(new Insets(15));
        historyList = new ListView<>();
        VBox.setVgrow(historyList, Priority.ALWAYS);
        refreshHistoryList();
        pane.setCenter(historyList);
        pane.setTop(new Label("Istoric Complet Comenzi:"));
        return pane;
    }

    private void refreshHistoryList() {
        if (historyList == null) return;
        javafx.concurrent.Task<List<Comanda>> loadTask = new javafx.concurrent.Task<>() {
            @Override protected List<Comanda> call() throws Exception { Thread.sleep(1000); return cmdRepo.getToateComenzile(); }
        };

        loadTask.setOnRunning(e -> {
            historyList.getItems().clear();
            historyList.getItems().add("⏳ Se încarcă datele din server...");
        });

        loadTask.setOnSucceeded(e -> {
            historyList.getItems().clear();
            List<Comanda> comenzi = loadTask.getValue();
            if (comenzi.isEmpty()) {
                historyList.getItems().add("Nu există comenzi.");
            } else {
                for (Comanda c : comenzi) {
                    if (!c.isFinalizata()) continue;
                    String ospatarName = (c.getOspatar() != null) ? c.getOspatar().getUsername() : "?";
                    StringBuilder sb = new StringBuilder();
                    sb.append(String.format("Masa %d | %s | %s\n", c.getMasa().getNumarMasa(), ospatarName, c.getDataOra().toString().substring(0, 16)));

                    Map<String, Integer> counts = new HashMap<>();
                    Map<String, Double> prices = new HashMap<>();
                    for (ComandaItem item : c.getItems()) {
                        String name = item.getProdus().getNume();
                        counts.put(name, counts.getOrDefault(name, 0) + item.getCantitate());
                        prices.put(name, item.getProdus().getPret());
                    }

                    for (String name : counts.keySet()) {
                        sb.append(String.format("    - %s x %d  (%.2f RON/buc)\n", name, counts.get(name), prices.get(name)));
                    }

                    double subtotal = c.calculeazaTotal();
                    double reducere = subtotal - c.getTotalPlata();
                    if(reducere > 0.01) {
                        sb.append(String.format("    (Oferte aplicate: -%.2f RON)\n", reducere));
                        for(OfferStrategy s : OfferManager.getInstance().getActiveOffers()) {
                            if(s.calculeazaReducere(c) > 0) sb.append("      * " + s.getNumeOferta() + "\n");
                        }
                    }
                    sb.append(String.format("    TOTAL: %.2f RON\n", c.getTotalPlata()));
                    historyList.getItems().add(sb.toString());
                }
            }
        });
        loadTask.setOnFailed(e -> historyList.getItems().add("Eroare DB!"));
        new Thread(loadTask).start();
    }
}