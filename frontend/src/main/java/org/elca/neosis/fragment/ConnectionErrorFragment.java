package org.elca.neosis.fragment;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.elca.neosis.factory.ObservableResourceFactory;
import org.jacpfx.api.annotations.fragment.Fragment;
import org.jacpfx.api.fragment.Scope;

@Fragment(
        id = ConnectionErrorFragment.ID,
        viewLocation = "",
        scope = Scope.PROTOTYPE,
        resourceBundleLocation = ObservableResourceFactory.RESOURCE_BUNDLE_NAME
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
