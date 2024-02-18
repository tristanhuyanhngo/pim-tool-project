package org.elca.neosis.common.ui;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import lombok.AllArgsConstructor;

import java.util.Optional;

@AllArgsConstructor
public class NotificationAlert {
    private String title;
    private String headerText;
    private String contentText;
    public void showConfirmationDialog() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }
}
