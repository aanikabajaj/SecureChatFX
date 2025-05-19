import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class InboxScreen {
    private Stage stage;
    private Scene scene;
    private String username;
    private ListView<String> messageList;
    private LinkedList<Message> messages;

    public InboxScreen(Stage stage, String username) {
        this.stage = stage;
        this.username = username;
        this.messages = ChatManager.inbox.get(username);
        setupUI();
    }

    private void setupUI() {
        VBox layout = new VBox(10);

        Label inboxLabel = new Label("Inbox:");
        messageList = new ListView<>();
        displayMessages(messages);

        messageList.setOnMouseClicked(event -> {
            String selectedItem = messageList.getSelectionModel().getSelectedItem();
            if (selectedItem != null && selectedItem.contains("[")) {
                int start = selectedItem.indexOf("[") + 1;
                int end = selectedItem.indexOf("]");
                String sender = selectedItem.substring(start, end);
                if (!sender.equals(username)) {
                    ChatWindow chatWindow = new ChatWindow(stage, username, sender);
                    stage.setScene(chatWindow.getScene());
                }
            }
        });

        // Search components
        Label searchLabel = new Label("Search messages by keyword:");
        TextField searchField = new TextField();
        Button searchButton = new Button("Search");

        searchButton.setOnAction(e -> {
            String keyword = searchField.getText().trim().toLowerCase();
            messageList.getItems().clear();

            if (keyword.isEmpty()) {
                messageList.getItems().add("Please enter a keyword to search.");
                return;
            }

            List<String> matchedMessages = new ArrayList<>();
            for (Message m : messages) {
                try {
                    BigInteger encryptedBig = new BigInteger(m.getContent());
                    String decrypted = ChatManager.getUsers().get(username).rsa.decrypt(encryptedBig);
                    if (decrypted.toLowerCase().contains(keyword)) {
                        matchedMessages.add(m.getTimestamp() + " [" + m.getSender() + "]: " + decrypted);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            if (matchedMessages.isEmpty()) {
                messageList.getItems().add("No messages found containing: \"" + keyword + "\"");
            } else {
                messageList.getItems().add("Search results for \"" + keyword + "\":");
                messageList.getItems().addAll(matchedMessages);
            }
        });

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
            ChatMenuScreen menu = new ChatMenuScreen(stage, username);
            stage.setScene(menu.getScene());
        });

        layout.getChildren().addAll(inboxLabel, messageList, searchLabel, searchField, searchButton, backButton);
        scene = new Scene(layout, 500, 450);
    }

    private void displayMessages(LinkedList<Message> msgs) {
        messageList.getItems().clear();
        for (Message m : msgs) {
            try {
                BigInteger encryptedBig = new BigInteger(m.getContent());
                String decrypted = ChatManager.getUsers().get(username).rsa.decrypt(encryptedBig);
                messageList.getItems().add(m.getTimestamp() + " [" + m.getSender() + "]: " + decrypted);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Scene getScene() {
        return scene;
    }
    
}