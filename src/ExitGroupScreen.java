import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ExitGroupScreen {
    private Scene scene;
    private Stage stage;
    private String username;

    public ExitGroupScreen(Stage stage, String username) {
        this.stage = stage;
        this.username = username;
        setupUI();
    }

    private void setupUI() {
        Label label = new Label("Exit a Group");

        TextField groupField = new TextField();
        groupField.setPromptText("Enter Group Name");

        Button exitButton = new Button("Exit Group");
        Button backButton = new Button("Back");

        exitButton.setOnAction(e -> {
            String groupName = groupField.getText().trim();
            if (groupName.isEmpty()) {
                showAlert("Error", "Please enter a group name.");
                return;
            }

            boolean success = GroupManager.exitGroup(groupName, username);
            if (success) {
                showAlert("Success", "You exited the group: " + groupName);
            } else {
                showAlert("Error", "Failed to exit group. Either group doesn't exist or you're not a member.");
            }

            groupField.clear();
        });

        backButton.setOnAction(e -> {
            stage.setScene(new ChatMenuScreen(stage, username).getScene());
        });

        VBox layout = new VBox(10, label, groupField, exitButton, backButton);
        layout.setStyle("-fx-padding: 20;");
        scene = new Scene(layout, 350, 200);
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
