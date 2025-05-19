import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.List;

public class GroupMessageScreen {
    private Scene scene;
    private Stage stage;
    private String username;
    private String groupName;

    private TextArea chatHistoryArea;
    private TextField messageField;

    public GroupMessageScreen(Stage stage, String username, String groupName) {
        this.stage = stage;
        this.username = username;
        this.groupName = groupName;
        setupUI();
    }

    private void setupUI() {
        Label titleLabel = new Label("Group Chat: " + groupName);

        chatHistoryArea = new TextArea();
        chatHistoryArea.setEditable(false);
        chatHistoryArea.setWrapText(true);
        loadChatHistory();

        messageField = new TextField();
        messageField.setPromptText("Type your message...");

        Button sendButton = new Button("Send");
        Button backButton = new Button("Back");

        sendButton.setOnAction(e -> sendMessage());
        backButton.setOnAction(e -> stage.setScene(new ViewGroupsScreen(stage, username).getScene()));

        VBox layout = new VBox(10, titleLabel, chatHistoryArea, messageField, sendButton, backButton);
        layout.setStyle("-fx-padding: 20;");
        this.scene = new Scene(layout, 450, 400);
    }

    private void loadChatHistory() {
        chatHistoryArea.clear();
        List<Message> messages = GroupManager.getGroupMessages(groupName);
        for (Message msg : messages) {
            String decrypted = Encryptor.decrypt(msg.getContent());
            chatHistoryArea.appendText(msg.getSender() + ": " + decrypted + "\n");
        }
    }

    private void sendMessage() {
        String text = messageField.getText().trim();
        if (text.isEmpty()) {
            showAlert("Error", "Message cannot be empty.");
            return;
        }

        if (ProfanityFilter.containsProfanity(text)) {
            showAlert("Blocked", "Message contains inappropriate content.");
            return;
        }

        String encrypted = Encryptor.encrypt(text);
        Message msg = new Message(username, groupName, encrypted); // groupName used just to show it's group-bound
        GroupManager.addGroupMessage(groupName, msg);

        messageField.clear();
        loadChatHistory();
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
