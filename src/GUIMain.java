import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GUIMain extends Application {

    @Override
    public void start(Stage primaryStage) {
        Button registerBtn = new Button("Register");
        Button loginBtn = new Button("Login");
        Button exitBtn = new Button("Exit");

        registerBtn.setOnAction(e -> {
            RegisterScreen registerScreen = new RegisterScreen(primaryStage);
            primaryStage.setScene(registerScreen.getScene());
        });
        
        loginBtn.setOnAction(e -> {
            LoginScreen loginScreen = new LoginScreen(primaryStage);
            primaryStage.setScene(loginScreen.getScene());
        });
        
        exitBtn.setOnAction(e -> primaryStage.close());

        VBox layout = new VBox(10);
        layout.getChildren().addAll(registerBtn, loginBtn, exitBtn);

        Scene scene = new Scene(layout, 300, 200);
        primaryStage.setTitle("Secure Messaging - GUI");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
