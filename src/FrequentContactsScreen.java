import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;

public class FrequentContactsScreen {
    private Scene scene;
    private Stage stage;
    private String username;

    public FrequentContactsScreen(Stage stage, String username) {
        this.stage = stage;
        this.username = username;
        setupUI();
    }

    private void setupUI() {
        VBox layout = new VBox(15);
        Label titleLabel = new Label("Your Frequent Contacts:");

        List<String> topContacts = ChatManager.chatRanker.getTopContacts(username, 10);

        if (topContacts.isEmpty()) {
            layout.getChildren().add(new Label("No frequent contacts found."));
        } else {
            for (String contactName : topContacts) {
                Label contactLabel = new Label("Contact: " + contactName);
                Button chatButton = new Button("Chat Now");
                chatButton.setOnAction(e ->
                        stage.setScene(new ChatWindow(stage, username, contactName).getScene())
                );
        
                VBox contactBox = new VBox(5, contactLabel, chatButton);
                contactBox.setStyle("-fx-padding: 10; -fx-border-color: gray; -fx-border-radius: 5;");
                layout.getChildren().add(contactBox);
            }
        }
        

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> stage.setScene(new ChatMenuScreen(stage, username).getScene()));

        layout.getChildren().add(backButton);
        layout.setStyle("-fx-padding: 20;");
        scene = new Scene(layout, 400, 500);
    }

    public Scene getScene() {
        return scene;
    }
}
