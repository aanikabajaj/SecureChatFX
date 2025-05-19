import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SendMessageScreen {
    private Stage stage;
    private Scene scene;
    private String fromUser; // current logged-in user

    public SendMessageScreen(Stage stage, String fromUser) {
        this.stage = stage;
        this.fromUser = fromUser;
        setupUI();
        
    }

    private void setupUI() {
        Label toLabel = new Label("Send to (Username):");
        TextField toField = new TextField();

        Label messageLabel = new Label("Message:");
        TextArea messageArea = new TextArea();

        Label delayLabel = new Label("Delay (in seconds, optional):");
        TextField delayField = new TextField();
        delayField.setPromptText("e.g., 5");

        Button sendButton = new Button("Send");
        Button backButton = new Button("Back");

        sendButton.setOnAction(e -> {
            String toUser = toField.getText().trim();
            String message = messageArea.getText().trim();
            String delayText = delayField.getText().trim();
        
            if (toUser.isEmpty() || message.isEmpty()) {
                showAlert("Please enter both recipient and message.");
                return;
            }
        
            if (ProfanityFilter.containsProfanity(message)) {
                showAlert("Your message contains inappropriate content. Please revise it.");
                return;
            }
        
            if (!GlobalContext.chatManager.getUsers().containsKey(toUser)) {
                showAlert("User does not exist.");
                return;
            }
        
            String encryptedMessage = GlobalContext.chatManager.getUsers().get(toUser).rsa.encrypt(message).toString();
        
            if (!delayText.isEmpty()) {
                try {
                    int delaySeconds = Integer.parseInt(delayText);
                    if (delaySeconds < 0) throw new NumberFormatException();
        
                    long deliveryTimeMillis = System.currentTimeMillis() + delaySeconds * 1000L;
                    int defaultPriority = 2; // Medium
                    ScheduledMessage sm = new ScheduledMessage(fromUser, toUser, encryptedMessage, deliveryTimeMillis, defaultPriority);

                    MessageScheduler.scheduleMessage(sm);
                    showAlert("Message scheduled successfully!");
        
                } catch (NumberFormatException ex) {
                    showAlert("Invalid delay value. Please enter a positive number.");
                }
            } else {
                boolean success = GlobalContext.chatManager.sendMessage(fromUser, toUser, message);
                if (success) {
                    showAlert("Message sent immediately!");
                    GlobalContext.chatRanker.recordInteraction(fromUser, toUser);
                } else {
                    showAlert("Failed to send message. User may not exist.");
                }
            }
        
            messageArea.clear();
            toField.clear();
            delayField.clear();
        });
        
        

        backButton.setOnAction(e -> {
            // Go back to ChatMenu
            stage.setScene(new ChatMenuScreen(stage, fromUser).getScene());
        });

        VBox layout = new VBox(10, toLabel, toField, messageLabel, messageArea, delayLabel, delayField, sendButton, backButton);
        scene = new Scene(layout, 350, 400);
    }
    
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING, message, ButtonType.OK);
        alert.showAndWait();
    }


    public Scene getScene() {
        return scene;
    }

    

}
