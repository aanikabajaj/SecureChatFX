import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginScreen {
    private Stage stage;
    private Scene scene;

    public LoginScreen(Stage stage) {
        this.stage = stage;
        setupUI();
    }

    private void setupUI() {
        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();

        Label passwordLabel = new Label("Password:");
        TextField passwordField = new TextField();

        Button loginButton = new Button("Login");
        Button backButton = new Button("Back");

        loginButton.setOnAction(e -> {
            String uname = usernameField.getText();
            String pwd = passwordField.getText();
        
            boolean success = GlobalContext.chatManager.loginUser(uname, pwd);
            if (success) {
                System.out.println("Login successful!");
                stage.setScene(new ChatMenuScreen(stage, uname).getScene()); // 👈 ADD THIS
            } else {
                System.out.println("Invalid credentials!");
            }
        });
        
        

        backButton.setOnAction(e -> {
            GUIMain guiMain = new GUIMain();
            try {
                guiMain.start(stage); // Back to main screen
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        VBox layout = new VBox(10, usernameLabel, usernameField, passwordLabel, passwordField, loginButton, backButton);
        scene = new Scene(layout, 300, 250);
    }

    public Scene getScene() {
        return scene;
    }
}
