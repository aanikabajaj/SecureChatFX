import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RegisterScreen {
    private Stage stage;
    private Scene scene;

    public RegisterScreen(Stage stage) {
        this.stage = stage;
        setupUI();
    }

    private void setupUI() {
        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();

        Label passwordLabel = new Label("Password:");
        TextField passwordField = new TextField();

        Button registerButton = new Button("Register");
        Button backButton = new Button("Back");

        registerButton.setOnAction(e -> {
            String uname = usernameField.getText();
            String pwd = passwordField.getText();
        
            boolean success = GlobalContext.chatManager.registerUser(uname, pwd);
            if (success) {
                System.out.println("Registration successful!");
                // You can show an Alert later if you want.
            } else {
                System.out.println("Username already exists!");
            }
        });
        

        backButton.setOnAction(e -> {
            GUIMain guiMain = new GUIMain();
            try {
                guiMain.start(stage); // Go back to main screen
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        VBox layout = new VBox(10, usernameLabel, usernameField, passwordLabel, passwordField, registerButton, backButton);
        scene = new Scene(layout, 300, 250);
    }

    public Scene getScene() {
        return scene;
    }
}
