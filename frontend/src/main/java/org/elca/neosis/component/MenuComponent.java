package org.elca.neosis.component;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import org.elca.neosis.config.JacpFXConfig;
import org.elca.neosis.factory.ObservableResourceFactory;
import org.elca.neosis.fragment.ProjectDetailFragment;
import org.elca.neosis.fragment.ProjectListFragment;
import org.jacpfx.api.annotations.Resource;
import org.jacpfx.api.annotations.component.DeclarativeView;
import org.jacpfx.api.message.Message;
import org.jacpfx.rcp.component.FXComponent;
import org.jacpfx.rcp.context.Context;

import javax.annotation.PostConstruct;

@DeclarativeView(
        name = MenuComponent.ID,
        id = MenuComponent.ID,
        viewLocation = "/fxml/MenuComponent.fxml",
        initialTargetLayoutId = JacpFXConfig.MENU_CONTAINER,
        resourceBundleLocation = ObservableResourceFactory.RESOURCE_BUNDLE_NAME

)
public class MenuComponent implements FXComponent {
    public static final String ID = "MenuComponent";

    @FXML
    private Label labelProjectLists;

    @FXML
    private Label labelNew;

    @FXML
    private Label labelNewProject;

    @FXML
    private Label labelNewCustomer;

    @FXML
    private Label labelNewSupplier;

    @Resource
    private Context context;

    @Override
    public Node postHandle(Node node, Message<Event, Object> message) throws Exception {
        return null;
    }

    @Override
    public Node handle(Message<Event, Object> message) throws Exception {
        return null;
    }

//    @PostConstruct
//    public void onPostConstructComponent() {
//        labelProjectLists.textProperty().bind(observableResourceFactory.getStringBinding(ApplicationBundleKey.LBL_VIEW_PROJECT_LIST));
//        labelNew.textProperty().bind(observableResourceFactory.getStringBinding(ApplicationBundleKey.LBL_NEW));
//        labelNewProject.textProperty().bind(observableResourceFactory.getStringBinding(ApplicationBundleKey.LBL_CREATE_NEW_PROJECT));
//        labelNewCustomer.textProperty().bind(observableResourceFactory.getStringBinding(ApplicationBundleKey.LBL_CREATE_NEW_CUSTOMER));
//        labelNewSupplier.textProperty().bind(observableResourceFactory.getStringBinding(ApplicationBundleKey.LBL_CREATE_NEW_SUPPLIER));
//        labelProjectLists.setOnMouseClicked(event -> context.send(MainContentComponent.ID, ProjectListFragment.ID));
//        labelNew.setOnMouseClicked(event -> context.send(MainContentComponent.ID, ProjectDetailFragment.ID));
//        labelNewProject.setOnMouseClicked(event -> context.send(MainContentComponent.ID, ProjectDetailFragment.ID));
//    }
}
