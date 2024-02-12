package org.elca.neosis.fragment;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import org.elca.neosis.factory.ObservableResourceFactory;
import org.jacpfx.api.annotations.Resource;
import org.jacpfx.api.annotations.fragment.Fragment;
import org.jacpfx.api.fragment.Scope;
import org.jacpfx.rcp.context.Context;

@Fragment(
        id = ProjectDetailFragment.ID,
        viewLocation = "/fxml/ProjectDetailFragment.fxml",
        scope = Scope.PROTOTYPE,
        resourceBundleLocation = ObservableResourceFactory.RESOURCE_BUNDLE_NAME
)
public class ProjectDetailFragment {
    public static final String ID = "ProjectDetailFragment";

    @Resource
    private Context context;

    @FXML
    private Button submitButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Label closeButton;

    @FXML
    private HBox errorContainer;

    @FXML
    private TextField projectNumberInput;

    @FXML
    private TextField projectNameInput;

    @FXML
    private TextField customerInput;

    @FXML
    private ComboBox<Long> groupInput;

    @FXML
    private TextField membersInput;

    @FXML
    private ComboBox<String> statusInput;

    @FXML
    private DatePicker startDateInput;

    @FXML
    private DatePicker endDateInput;
}
