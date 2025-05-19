import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.io.FileWriter;

public class GroupChatScreen {
    private Scene scene;
    private Stage stage;
    private String username;
    private String groupName;
    private VBox messageDisplay;
    private TextField messageField;
    private Label typingIndicator;

    public GroupChatScreen(Stage stage, String username, String groupName) {
        this.stage = stage;
        this.username = username;
        this.groupName = groupName;
        setupUI();
        loadChatHistory();
    }

    private void setupUI() {
        Label titleLabel = new Label("Group Chat: " + groupName);
        messageDisplay = new VBox(5);
        messageDisplay.setStyle("-fx-background-color: #F9F9F9; -fx-padding: 10;");

        ScrollPane scrollPane = new ScrollPane(messageDisplay);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(400);

        messageField = new TextField();
        messageField.setPromptText("Type your message...");

        typingIndicator = new Label();
        typingIndicator.setStyle("-fx-text-fill: gray; -fx-font-style: italic;");

        Button sendButton = new Button("Send");
        sendButton.setOnAction(e -> sendMessage());

        Button exportButton = new Button("Export Compressed Chat History");
        exportButton.setOnAction(e -> exportCompressedChat());

        Button viewDecompressedButton = new Button("View Decompressed Chat History");
        viewDecompressedButton.setOnAction(e -> viewDecompressedChat());

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> stage.setScene(new ViewGroupsScreen(stage, username).getScene()));

        HBox inputBox = new HBox(10, messageField, sendButton);
        VBox layout = new VBox(10, titleLabel, scrollPane, typingIndicator, inputBox, exportButton, viewDecompressedButton, backButton);
        layout.setStyle("-fx-padding: 20;");
        scene = new Scene(layout, 500, 600);

        // Typing detection
        javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(3));
        messageField.setOnKeyTyped(e -> {
            typingIndicator.setText("You are typing...");
            pause.playFromStart();
        });
        pause.setOnFinished(e -> typingIndicator.setText(""));
    }

    private void loadChatHistory() {
        List<Message> messages = GroupManager.getGroupMessages(groupName);
        messageDisplay.getChildren().clear();
        for (Message msg : messages) {
            String decrypted = Encryptor.decrypt(msg.getContent());
            Label label = new Label(msg.getSender() + ": " + decrypted);
            messageDisplay.getChildren().add(label);
        }
    }

    private void sendMessage() {
        String content = messageField.getText().trim();
        if (content.isEmpty()) return;

        if (ProfanityFilter.containsProfanity(content)) {
            showAlert("Blocked", "Message contains inappropriate content.");
            return;
        }

        String encrypted = Encryptor.encrypt(content);
        Message message = new Message(username, groupName, encrypted);

        GroupManager.addGroupMessage(groupName, message);

        Set<String> members = GroupManager.getGroupMembers(groupName);
        for (String member : members) {
            if (!member.equals(username)) {
                Message userMessage = new Message(username, member, encrypted);
                if (ChatManager.inbox.containsKey(member)) {
                    ChatManager.inbox.get(member).add(userMessage);
                } else {
                    MessageQueue.addMessage(member, userMessage);
                }
                ChatManager.chatRanker.recordInteraction(username, member);
            }
        }

        Label label = new Label(username + ": " + content);
        messageDisplay.getChildren().add(label);
        messageField.clear();
        typingIndicator.setText("");
    }

    private void exportCompressedChat() {
        List<Message> messages = GroupManager.getGroupMessages(groupName);
        if (messages.isEmpty()) {
            showAlert("Info", "No chat history to export.");
            return;
        }

        StringBuilder chatBuilder = new StringBuilder();
        for (Message msg : messages) {
            String decrypted = Encryptor.decrypt(msg.getContent());
            chatBuilder.append(msg.getSender())
                       .append(": ")
                       .append(decrypted)
                       .append("\n");
        }

        String chatText = chatBuilder.toString();
        String compressed = HuffmanCompressor.compress(chatText);

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Compressed Chat");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(compressed);
                showAlert("Success", "Chat history exported successfully!");
            } catch (IOException e) {
                showAlert("Error", "Failed to save the file.");
            }
        }
    }

    private void viewDecompressedChat() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Compressed Chat File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            try {
                String compressedContent = Files.readString(file.toPath());
                String decompressed = HuffmanCompressor.decompress(compressedContent);

                TextArea textArea = new TextArea(decompressed);
                textArea.setWrapText(true);
                textArea.setEditable(false);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Decompressed Chat History");
                alert.getDialogPane().setContent(new ScrollPane(textArea));
                alert.setResizable(true);
                alert.showAndWait();

            } catch (IOException e) {
                showAlert("Error", "Failed to read the file.");
            } catch (Exception e) {
                showAlert("Error", "Invalid compressed file format.");
            }
        }
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public Scene getScene() {
        return scene;
    }
}
