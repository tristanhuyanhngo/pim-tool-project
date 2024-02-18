package org.elca.neosis.common.ui;

import javafx.scene.control.Alert;
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
    private String confirmButtonColor;
    private String cancelButtonColor;
    private Alert.AlertType type;

    public boolean showConfirmationDialog() {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

        ButtonType confirmButton = new ButtonType(confirmButtonText, ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType(cancelButtonText, ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(confirmButton, cancelButton);

        alert.getDialogPane().lookupButton(confirmButton).setStyle("-fx-base: " + confirmButtonColor + ";");

        alert.getDialogPane().lookupButton(cancelButton).setStyle("-fx-base: " + cancelButtonColor + ";");

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == confirmButton;
    }
}


