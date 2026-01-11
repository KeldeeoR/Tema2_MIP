package org.example;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class LoginView {

    private final Stage stage;
    private final MainApp mainApp; // Referinta catre MainApp ca sa putem schimba scena
    private final UserRepository userRepo;

    public LoginView(Stage stage, MainApp mainApp) {
        this.stage = stage;
        this.mainApp = mainApp;
        this.userRepo = new UserRepository();
    }

    public void show() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #f4f4f4;"); // Un gri deschis elegant

        // Titlu
        Label titleLabel = new Label("Restaurant Login");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        // Campuri
        TextField userField = new TextField();
        userField.setPromptText("Username");
        userField.setMaxWidth(250);

        PasswordField passField = new PasswordField();
        passField.setPromptText("Password");
        passField.setMaxWidth(250);

        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);

        // Butoane
        Button loginButton = new Button("Autentificare");
        loginButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        loginButton.setPrefWidth(250);

        Button guestButton = new Button("Continuă ca Vizitator (Guest)");
        guestButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        guestButton.setPrefWidth(250);

        // --- ACTIUNI ---

        // 1. Logica de Login (Staff/Admin)
        loginButton.setOnAction(e -> {
            String user = userField.getText();
            String pass = passField.getText();

            // Folosim UserRepository creat anterior
            userRepo.login(user, pass).ifPresentOrElse(
                    foundUser -> {
                        System.out.println("Login reusit: " + foundUser.getRole());
                        // --- MODIFICARE AICI: Trimitem obiectul User, nu doar rolul ---
                        mainApp.openMainInterface(foundUser);
                    },
                    () -> errorLabel.setText("Username sau parolă incorecte!")
            );
        });

        // 2. Logica de Guest
        guestButton.setOnAction(e -> {
            System.out.println("Intrare ca Guest");
            // --- MODIFICARE AICI: Cream un user temporar "Guest" ---
            // Trimitem un user cu rol NULL, dar care are numele "Guest" pentru afisaj
            User guestUser = new User("Guest", "", null);
            mainApp.openMainInterface(guestUser);
        });

        root.getChildren().addAll(titleLabel, userField, passField, loginButton, new Label("sau"), guestButton, errorLabel);

        Scene scene = new Scene(root, 400, 450);
        stage.setScene(scene);
        stage.setTitle("Login - Restaurant App");
        stage.show();
    }
}