import javafx.animation.PauseTransition;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.math.BigInteger;
import java.util.*;

public class ChatWindow {
    private Stage stage;
    private Scene scene;
    private String currentUser;
    private String chatWith;
    private Label typingIndicator;

    public ChatWindow(Stage stage, String currentUser, String chatWith) {
        this.stage = stage;
        this.currentUser = currentUser;
        this.chatWith = chatWith;
        setupUI();
    }

    private void setupUI() {
        VBox layout = new VBox(10);
        Label title = new Label("Chat with " + chatWith);

        ListView<String> chatList = new ListView<>();
        LinkedList<Message> conversation = ChatManager.conversations
                .getOrDefault(currentUser, new HashMap<>())
                .getOrDefault(chatWith, new LinkedList<>());

        for (Message m : conversation) {
            BigInteger encrypted = new BigInteger(m.getContent());
            String decrypted = ChatManager.getUsers().get(currentUser).rsa.decrypt(encrypted);
            chatList.getItems().add(m.getTimestamp() + " [" + m.getSender() + "]: " + decrypted);
        }

        TextField messageField = new TextField();
        Label delayLabel = new Label("Delay (in seconds, optional):");
        TextField delayField = new TextField();
        delayField.setPromptText("e.g., 10");

        // 🔥 NEW: Priority dropdown
        Label priorityLabel = new Label("Priority (for scheduled messages):");
        ComboBox<String> priorityCombo = new ComboBox<>();
        priorityCombo.getItems().addAll("High", "Medium", "Low");
        priorityCombo.setValue("Medium");  // Default

        typingIndicator = new Label();

        Button sendButton = new Button("Send");

        // ✅ Typing indicator logic
        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        messageField.setOnKeyTyped(e -> {
            typingIndicator.setText("You are typing...");
            pause.playFromStart();
        });
        pause.setOnFinished(e -> typingIndicator.setText(""));

        sendButton.setOnAction(e -> {
            String text = messageField.getText().trim();
            String delayText = delayField.getText().trim();
            String priority = priorityCombo.getValue();
            // Convert priority string to int
            int priorityValue;
            switch (priority) {
                case "High": priorityValue = 1; break;
                case "Low": priorityValue = 3; break;
                default: priorityValue = 2; break; // Default is Medium
            }


            if (text.isEmpty()) return;

            if (ProfanityFilter.containsProfanity(text)) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Message contains profanity. Not allowed.");
                alert.showAndWait();
                return;
            }

            RSAUtil receiverRSA = ChatManager.getUsers().get(chatWith).rsa;
            BigInteger encrypted = receiverRSA.encrypt(text);

            if (!delayText.isEmpty()) {
                try {
                    int delay = Integer.parseInt(delayText);
                    if (delay < 0) throw new NumberFormatException();

                    long deliveryTime = System.currentTimeMillis() + delay * 1000L;
                    // 🔥 Pass priority to ScheduledMessage
                    ScheduledMessage sm = new ScheduledMessage(currentUser, chatWith, encrypted.toString(), deliveryTime, priorityValue);
                    MessageScheduler.scheduleMessage(sm);

                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Message scheduled successfully!\nPriority: " + priority);
                    alert.showAndWait();
                } catch (NumberFormatException ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid delay. Please enter a positive number.");
                    alert.showAndWait();
                }
            } else {
                Message msg = new Message(currentUser, chatWith, encrypted.toString());

                ChatManager.inbox.get(chatWith).add(msg);
                ChatManager.addMessageToConversation(currentUser, chatWith, msg);

                chatList.getItems().add(msg.getTimestamp() + " [" + currentUser + "]: " + text);
            }

            messageField.clear();
            delayField.clear();
            typingIndicator.setText("");
        });

        Button blockButton = new Button("Block User");
        blockButton.setOnAction(e -> {
            boolean success = ChatManager.blockUser(currentUser, chatWith);
            if (success) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, chatWith + " has been blocked.");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "You have already blocked " + chatWith + ".");
                alert.showAndWait();
            }
        });

        Button manageBlockedButton = new Button("Manage Blocked Users");
        manageBlockedButton.setOnAction(e -> {
            BlockedUsersWindow blockedUsersWindow = new BlockedUsersWindow(stage, currentUser);
            stage.setScene(blockedUsersWindow.getScene());
        });

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
            InboxScreen inbox = new InboxScreen(stage, currentUser);
            stage.setScene(inbox.getScene());
        });

        layout.getChildren().addAll(
                title,
                chatList,
                typingIndicator,
                messageField,
                delayLabel,
                delayField,
                priorityLabel,    // 🔥 Add priority dropdown
                priorityCombo,    // 🔥 Add priority dropdown
                sendButton,
                blockButton,
                manageBlockedButton,
                backButton
        );

        scene = new Scene(layout, 500, 520);
    }

    public Scene getScene() {
        return scene;
    }
}
