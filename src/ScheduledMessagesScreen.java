import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class ScheduledMessagesScreen {
    private Stage stage;
    private Scene scene;
    private String currentUser;

    public ScheduledMessagesScreen(Stage stage, String currentUser) {
        this.stage = stage;
        this.currentUser = currentUser;
        setupUI();
    }

    private void setupUI() {
        Label title = new Label("Your Scheduled Messages");

        TableView<ScheduledMessage> table = new TableView<>();
        TableColumn<ScheduledMessage, String> toCol = new TableColumn<>("To");
        TableColumn<ScheduledMessage, String> messageCol = new TableColumn<>("Message");
        TableColumn<ScheduledMessage, String> timeCol = new TableColumn<>("Delivery Time");
        TableColumn<ScheduledMessage, Void> actionCol = new TableColumn<>("Action");

        toCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().receiver));
        messageCol.setCellValueFactory(data -> {
            RSAUtil rsa = ChatManager.getUsers().get(currentUser).rsa;
            BigInteger encrypted = new BigInteger(data.getValue().encryptedMessage);
            String decrypted = rsa.decrypt(encrypted);
            return new javafx.beans.property.SimpleStringProperty(decrypted);
        });
        timeCol.setCellValueFactory(data -> {
            Date date = new Date(data.getValue().deliveryTimeMillis);
            String formatted = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
            return new javafx.beans.property.SimpleStringProperty(formatted);
        });

        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button cancelBtn = new Button("Cancel");

            {
                cancelBtn.setOnAction(e -> {
                    ScheduledMessage sm = getTableView().getItems().get(getIndex());
                    boolean removed = MessageScheduler.removeScheduledMessage(sm);
                    if (removed) {
                        getTableView().getItems().remove(sm);
                    } else {
                        showAlert("Could not cancel message.");
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(cancelBtn);
                }
            }
        });

        table.getColumns().addAll(toCol, messageCol, timeCol, actionCol);

        ObservableList<ScheduledMessage> userMessages = FXCollections.observableArrayList(
                MessageScheduler.getAllScheduledMessages()
                        .stream()
                        .filter(sm -> sm.sender.equals(currentUser))
                        .collect(Collectors.toList())
        );

        table.setItems(userMessages);

        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> stage.setScene(new ChatMenuScreen(stage, currentUser).getScene()));

        VBox layout = new VBox(10, title, table, backBtn);
        scene = new Scene(layout, 600, 400);
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK);
        alert.showAndWait();
    }

    public Scene getScene() {
        return scene;
    }
}
