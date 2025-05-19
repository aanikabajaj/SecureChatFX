import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ManageGroupsScreen {
    private Stage stage;
    private Scene scene;
    private String username;

    public ManageGroupsScreen(Stage stage, String username) {
        this.stage = stage;
        this.username = username;
        setupUI();
    }

    private void setupUI() {
        Button createGroupBtn = new Button("Create Group");
        Button joinGroupBtn = new Button("Join Group");
        Button viewGroupsBtn = new Button("View My Groups");
        Button backButton = new Button("Back");

        createGroupBtn.setOnAction(e -> stage.setScene(new CreateGroupScreen(stage, username).getScene()));
        joinGroupBtn.setOnAction(e -> stage.setScene(new JoinGroupScreen(stage, username).getScene()));
        viewGroupsBtn.setOnAction(e -> stage.setScene(new ViewGroupsScreen(stage, username).getScene()));
        backButton.setOnAction(e -> stage.setScene(new ChatMenuScreen(stage, username).getScene()));

        VBox layout = new VBox(15, createGroupBtn, joinGroupBtn, viewGroupsBtn, backButton);
        scene = new Scene(layout, 300, 200);
    }

    public Scene getScene() {
        return scene;
    }
}
