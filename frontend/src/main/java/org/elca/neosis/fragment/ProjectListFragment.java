package org.elca.neosis.fragment;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import org.elca.neosis.factory.ObservableResourceFactory;
import org.jacpfx.api.annotations.fragment.Fragment;
import org.jacpfx.api.fragment.Scope;

@Fragment(
        id = ProjectListFragment.ID,
        viewLocation = "/fxml/ProjectListFragment.fxml",
        scope = Scope.PROTOTYPE,
        resourceBundleLocation = ObservableResourceFactory.RESOURCE_BUNDLE_NAME
)
public class ProjectListFragment {
    public static final String ID = "ProjectListFragment";

    @FXML
    private TextField searchBar;

    @FXML
    private Label resetButton;

    @FXML
    private ComboBox<String> projectStatus;

    @FXML
    private Label numberOfSelectedItems;

    @FXML
    private HBox selectedItemsContainer;

    @FXML
    private ImageView deleteAllButton;

    @FXML
    private Button searchButton;
}
