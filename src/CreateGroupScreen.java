import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CreateGroupScreen {
    private Scene scene;
    private Stage stage;
    private String username;

    public CreateGroupScreen(Stage stage, String username) {
        this.stage = stage;
        this.username = username;
        setupUI();
    }

    private void setupUI() {
        Label titleLabel = new Label("Create New Group");
        TextField groupNameField = new TextField();
        groupNameField.setPromptText("Enter Group Name");

        Button createButton = new Button("Create Group");
        Button backButton = new Button("Back");

        createButton.setOnAction(e -> {
            String groupName = groupNameField.getText().trim();
            if (groupName.isEmpty()) {
                showAlert("Error", "Group name cannot be empty.");
                return;
            }

            if (GroupManager.groupExists(groupName)) {
                showAlert("Error", "Group already exists.");
                return;
            }

            GroupManager.createGroup(groupName, username);
            showAlert("Success", "Group created successfully.");
            groupNameField.clear();
        });

        backButton.setOnAction(e -> {
            stage.setScene(new ChatMenuScreen(stage, username).getScene());
        });

        VBox layout = new VBox(10, titleLabel, groupNameField, createButton, backButton);
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
