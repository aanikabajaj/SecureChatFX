import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class JoinGroupScreen {
    private Scene scene;
    private Stage stage;
    private String username;

    public JoinGroupScreen(Stage stage, String username) {
        this.stage = stage;
        this.username = username;
        setupUI();
    }

    private void setupUI() {
        Label titleLabel = new Label("Join Group");
        TextField groupNameField = new TextField();
        groupNameField.setPromptText("Enter Group Name");

        Button joinButton = new Button("Join");
        Button backButton = new Button("Back");

        joinButton.setOnAction(e -> {
            String groupName = groupNameField.getText().trim();
            if (groupName.isEmpty()) {
                showAlert("Error", "Group name cannot be empty.");
                return;
            }

            if (GroupManager.joinGroup(groupName, username)) {
                showAlert("Success", "Successfully joined the group!");
            } else {
                showAlert("Error", "Group not found.");
            }

            groupNameField.clear();
        });

        backButton.setOnAction(e -> {
            stage.setScene(new ChatMenuScreen(stage, username).getScene());
        });

        VBox layout = new VBox(10, titleLabel, groupNameField, joinButton, backButton);
        layout.setStyle("-fx-padding: 20;");
        this.scene = new Scene(layout, 300, 200);
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
