package org.elca.neosis.common.ui;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import lombok.AllArgsConstructor;

import java.util.Optional;

@AllArgsConstructor
public class ConfirmationAlert {
    private String title;
    private String headerText;
    private String contentText;
    private String confirmButtonText;
    private String cancelButtonText;
    private Alert.AlertType type;

    public boolean showConfirmationDialog() {
        Alert alert = createConfirmationAlert();
        configureDialogButtons(alert);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.OK_DONE;
    }

    private Alert createConfirmationAlert() {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        return alert;
    }

    private void configureDialogButtons(Alert alert) {
        ButtonType confirmButton = new ButtonType(confirmButtonText, ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType(cancelButtonText, ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(confirmButton, cancelButton);

        configureButtonDefaultBehavior(alert, confirmButton, false);
        configureButtonDefaultBehavior(alert, cancelButton, true);
    }

    private void configureButtonDefaultBehavior(Alert alert, ButtonType buttonType, boolean isDefault) {
        Button button = (Button) alert.getDialogPane().lookupButton(buttonType);
        button.setDefaultButton(isDefault);
    }

}


