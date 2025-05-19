import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AddGroupMemberScreen {
    private Scene scene;
    private Stage stage;
    private String username;

    public AddGroupMemberScreen(Stage stage, String username, String groupName) {
        this.stage = stage;
        this.username = username;
        setupUI(groupName);
    }
    

    private void setupUI(String groupName) {
        Label titleLabel = new Label("Add Member to Group: " + groupName);
    
        TextField userField = new TextField();
        userField.setPromptText("Enter Username to Add");
    
        Button addButton = new Button("Add Member");
        Button backButton = new Button("Back");
    
        addButton.setOnAction(e -> {
            String newMember = userField.getText().trim();
    
            if (newMember.isEmpty()) {
                showAlert("Error", "Username is required.");
                return;
            }
    
            if (!ChatManager.getUsers().containsKey(newMember)) {
                showAlert("Error", "User does not exist.");
                return;
            }
    
            if (!GroupManager.groupExists(groupName)) {
                showAlert("Error", "Group not found.");
                return;
            }
    
            GroupManager.joinGroup(groupName, newMember);
            showAlert("Success", newMember + " added to group " + groupName + ".");
            userField.clear();
        });
    
        backButton.setOnAction(e -> {
            stage.setScene(new ViewGroupsScreen(stage, username).getScene());
        });
    
        VBox layout = new VBox(10, titleLabel, userField, addButton, backButton);
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
