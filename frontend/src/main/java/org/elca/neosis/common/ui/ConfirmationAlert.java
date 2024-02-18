package org.elca.neosis.common.ui;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import lombok.AllArgsConstructor;

import java.util.Optional;

@AllArgsConstructor
public class ConfirmationAlert {
    // Di chuyển ra 1 package các component tiện ích trên 1 cái fragment
    private final String title;
    private final String headerText;
    private final String contentText;
    private final String confirmButtonText;
    private final String cancelButtonText;
    private final String confirmButtonColor;
    private final String cancelButtonColor;

    public boolean showConfirmationDialog() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

        ButtonType confirmButton = new ButtonType(confirmButtonText, ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType(cancelButtonText, ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(confirmButton, cancelButton);

        // Thiết lập màu cho nút Confirm
        alert.getDialogPane().lookupButton(confirmButton).setStyle("-fx-base: " + confirmButtonColor + ";");

        // Thiết lập màu cho nút Cancel
        alert.getDialogPane().lookupButton(cancelButton).setStyle("-fx-base: " + cancelButtonColor + ";");

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == confirmButton;
    }
}


