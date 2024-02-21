package org.elca.neosis.fragment;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.elca.neosis.multilingual.I18N;
import org.jacpfx.api.annotations.fragment.Fragment;
import org.jacpfx.api.fragment.Scope;

@Fragment(
        id = ConnectionErrorFragment.ID,
        viewLocation = "fxml/ConnectionErrorFragment.fxml",
        resourceBundleLocation = I18N.BUNDLE_NAME,
        scope = Scope.PROTOTYPE
)
public class ConnectionErrorFragment {
    public static final String ID = "ConnectionErrorFragment";
    @FXML
    private Label unexpectedError;

    @FXML
    private Label please;

    @FXML
    private Label contactYourAdministrator;

    @FXML
    private Label or;

    @FXML
    private Label backToSearchProject;

    public void init() {
        unexpectedError.setText("Unexpected error occurred");
        please.setText("Please");
        contactYourAdministrator.setText("contact your administrator");
        or.setText("or");
        backToSearchProject.setText("back to search project");
    }
}
