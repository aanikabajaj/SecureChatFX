import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Set;

public class ViewGroupsScreen {
    private Stage stage;
    private Scene scene;
    private String username;

    public ViewGroupsScreen(Stage stage, String username) {
        this.stage = stage;
        this.username = username;
        setupUI();
    }

    private void setupUI() {
        VBox layout = new VBox(15);
        Label title = new Label("Your Groups:");

        boolean hasGroups = false;
        for (String groupName : GroupManager.getAllGroups()) {
            Set<String> members = GroupManager.getGroupMembers(groupName);
            if (members.contains(username)) {
                hasGroups = true;

                Label groupLabel = new Label("Group: " + groupName);

                Button messageGroupBtn = new Button("Message Group");
                messageGroupBtn.setOnAction(e ->
                        stage.setScene(new GroupMessageScreen(stage, username, groupName).getScene())
                );

                Button addMembersBtn = new Button("Add Members");
                addMembersBtn.setOnAction(e ->
                        stage.setScene(new AddGroupMemberScreen(stage, username, groupName).getScene())
                );

                Button exitGroupBtn = new Button("Exit Group");
                exitGroupBtn.setOnAction(e -> {
                    GroupManager.exitGroup(groupName, username);
                    showAlert("You have exited the group: " + groupName);
                    stage.setScene(new ViewGroupsScreen(stage, username).getScene()); // refresh
                });

                VBox groupBox = new VBox(5, groupLabel, messageGroupBtn, addMembersBtn, exitGroupBtn);
                groupBox.setStyle("-fx-padding: 10; -fx-border-color: gray; -fx-border-radius: 5;");
                layout.getChildren().add(groupBox);
            }
        }

        if (!hasGroups) {
            layout.getChildren().add(new Label("You are not in any groups."));
        }

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> stage.setScene(new ManageGroupsScreen(stage, username).getScene()));
        layout.getChildren().addAll(title, backButton);

        scene = new Scene(layout, 350, 400);
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Group Info");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public Scene getScene() {
        return scene;
    }
}
