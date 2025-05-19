import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.*;

public class BlockedUsersWindow {
    private Scene scene;

    public BlockedUsersWindow(Stage stage, String currentUser) {
        VBox layout = new VBox(10);
        Label title = new Label("Blocked Users");
        ObservableList<String> blockedUsersList = FXCollections.observableArrayList();
        ListView<String> blockedList = new ListView<>(blockedUsersList);

        Set<String> blockedUsers = BlockManager.getBlockedUsers(currentUser);
        if (blockedUsers != null) {
            blockedList.getItems().addAll(blockedUsers);
        }

        Button unblockButton = new Button("Unblock Selected");
        unblockButton.setOnAction(e -> {
            String selectedUser = blockedList.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                BlockManager.unblockUser(currentUser, selectedUser);
                blockedList.getItems().remove(selectedUser);
            }
        });

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
            ChatMenuScreen mainMenu = new ChatMenuScreen(stage, currentUser);
            stage.setScene(mainMenu.getScene());
        });

        layout.getChildren().addAll(title, blockedList, unblockButton, backButton);
        scene = new Scene(layout, 400, 300);
    }

    public Scene getScene() {
        return scene;
    }
}
