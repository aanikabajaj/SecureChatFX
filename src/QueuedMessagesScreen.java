import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class QueuedMessagesScreen {
    private Stage stage;
    private Scene scene;
    private String username;

    public QueuedMessagesScreen(Stage stage, String username) {
        this.stage = stage;
        this.username = username;
        setupUI();
    }

    private void setupUI() {
        VBox layout = new VBox(15);
        Label titleLabel = new Label("Queued Messages for You:");

        List<Message> queued = new ArrayList<>(MessageQueue.getMessages(username));

        if (queued == null || queued.isEmpty()) {
            layout.getChildren().add(new Label("No queued messages."));
        } else {
            for (Message msg : queued) {
                String decryptedText = Encryptor.decrypt(msg.getContent());
                Label msgLabel = new Label(
                    "From: " + msg.getSender() +
                    "\nMessage: " + decryptedText +
                    "\nScheduled At: " + msg.getTimestamp()
                );
                msgLabel.setStyle("-fx-border-color: gray; -fx-padding: 10;");
                layout.getChildren().add(msgLabel);
            }
        }

        Button backButton = new Button("Back");
        backButton.setOnAction(e ->
            stage.setScene(new ChatMenuScreen(stage, username).getScene())
        );

        layout.getChildren().addAll(titleLabel, backButton);
        layout.setStyle("-fx-padding: 20;");
        scene = new Scene(layout, 400, 400);
    }

    public Scene getScene() {
        return scene;
    }
}
