import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ChatMenuScreen {
    private Stage stage;
    private Scene scene;
    private String username;

    public ChatMenuScreen(Stage stage, String username) {
        this.stage = stage;
        this.username = username;
        setupUI();
    }

    private void setupUI() {
        Button sendMessageButton = new Button("Send Message");
        Button viewInboxButton = new Button("View Inbox");
        Button viewScheduledButton = new Button("View Scheduled Messages");
        Button manageBlockedButton = new Button("Manage Blocked Users");
        Button manageGroupsButton = new Button("Manage Groups");
        Button frequentContactsButton = new Button("Frequent Contacts");
        Button viewQueueBtn = new Button("View Queued Messages");
        Button logoutButton = new Button("Log Out");

        sendMessageButton.setOnAction(e -> {
            SendMessageScreen sendScreen = new SendMessageScreen(stage, username);
            stage.setScene(sendScreen.getScene());
        });

        viewInboxButton.setOnAction(e -> {
            InboxScreen inboxScreen = new InboxScreen(stage, username);
            stage.setScene(inboxScreen.getScene());
        });

        viewScheduledButton.setOnAction(e -> {
            ScheduledMessagesScreen scheduledScreen = new ScheduledMessagesScreen(stage, username);
            stage.setScene(scheduledScreen.getScene());
        });

        manageBlockedButton.setOnAction(e -> {
            BlockedUsersWindow blockedUsersWindow = new BlockedUsersWindow(stage, username);
            stage.setScene(blockedUsersWindow.getScene());
        });

        manageGroupsButton.setOnAction(e -> stage.setScene(new ManageGroupsScreen(stage, username).getScene()));

        frequentContactsButton.setOnAction(e -> 
            stage.setScene(new FrequentContactsScreen(stage, username).getScene())
        );

        viewQueueBtn.setOnAction(e ->
            stage.setScene(new QueuedMessagesScreen(stage, username).getScene())
        );

        logoutButton.setOnAction(e -> {
            LoginScreen loginScreen = new LoginScreen(stage);
            stage.setScene(loginScreen.getScene());
        });

        VBox layout = new VBox(15, sendMessageButton, viewInboxButton, viewScheduledButton, manageBlockedButton, manageGroupsButton, frequentContactsButton, viewQueueBtn, logoutButton);
        scene = new Scene(layout, 300, 250);
    }

    public Scene getScene() {
        return scene;
    }
}
