package org.elca.neosis.fragment;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.elca.neosis.common.ApplicationBundleKey;
import org.elca.neosis.multilingual.I18N;
import org.jacpfx.api.annotations.fragment.Fragment;
import org.jacpfx.api.fragment.Scope;

@Fragment(
        id = ConnectionErrorFragment.ID,
        viewLocation = "/fxml/ConnectionErrorFragment.fxml",
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
        unexpectedError.textProperty().bind(I18N.createStringBinding(ApplicationBundleKey.LABEL_CONNECTION_ERROR_FRAGMENT_UNEXPECTED_ERROR_OCCURRED));
        please.textProperty().bind(I18N.createStringBinding(ApplicationBundleKey.LABEL_CONNECTION_ERROR_FRAGMENT_PLEASE));
        contactYourAdministrator.textProperty().bind(I18N.createStringBinding(ApplicationBundleKey.LABEL_CONNECTION_ERROR_FRAGMENT_CONTACT_YOUR_ADMIN));
        or.textProperty().bind(I18N.createStringBinding(ApplicationBundleKey.LABEL_CONNECTION_ERROR_FRAGMENT_OR));
        backToSearchProject.textProperty().bind(I18N.createStringBinding(ApplicationBundleKey.LABEL_CONNECTION_ERROR_FRAGMENT_BACK_TO_SEARCH_PROJECT));
    }
}
