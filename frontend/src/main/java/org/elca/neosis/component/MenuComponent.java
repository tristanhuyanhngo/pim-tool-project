package org.elca.neosis.component;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import org.elca.neosis.config.JacpFXConfig;
import org.elca.neosis.factory.ObservableResourceFactory;
import org.jacpfx.api.annotations.Resource;
import org.jacpfx.api.annotations.component.DeclarativeView;
import org.jacpfx.api.message.Message;
import org.jacpfx.rcp.component.FXComponent;
import org.jacpfx.rcp.context.Context;

@DeclarativeView(
        name = MenuComponent.ID,
        id = MenuComponent.ID,
        viewLocation = "/fxml/MenuComponent.fxml",
        initialTargetLayoutId = JacpFXConfig.MENU_CONTAINER,
        resourceBundleLocation = ObservableResourceFactory.RESOURCE_BUNDLE_NAME
)
public class MenuComponent implements FXComponent {
    public static final String ID = "MenuComponent";

    //    @Autowired
//    private ObservableResourceFactory observableResourceFactory;

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
}
