package org.elca.neosis.component;

import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.elca.neosis.config.JacpFXConfig;
import org.elca.neosis.fragment.ProjectDetailFragment;
import org.elca.neosis.fragment.ProjectListFragment;
import org.jacpfx.api.annotations.Resource;
import org.jacpfx.api.annotations.component.View;
import org.jacpfx.api.message.Message;
import org.jacpfx.rcp.component.FXComponent;
import org.jacpfx.rcp.components.managedFragment.ManagedFragmentHandler;
import org.jacpfx.rcp.context.Context;

@View(
        name = MainContentComponent.ID,
        id = MainContentComponent.ID,
        initialTargetLayoutId = JacpFXConfig.MAIN_CONTENT_CONTAINER
)
public class MainContentComponent implements FXComponent {
    public static final String ID = "MainContentComponent";

    private Node root;

    @Resource
    private Context context;

    @Override
    public Node postHandle(Node node, Message<Event, Object> message) throws Exception {
        this.root = initProjectListFragment();

        return this.root;
    }

    @Override
    public Node handle(Message<Event, Object> message) throws Exception {
        return null;
    }

    private Node initProjectListFragment() {
        final VBox container = new VBox();
        VBox.setVgrow(container, Priority.ALWAYS);
        final ManagedFragmentHandler<ProjectListFragment> handler = context.getManagedFragmentHandler(ProjectListFragment.class);
        final ProjectListFragment controller = handler.getController();
        controller.init();
        container.getChildren().addAll(handler.getFragmentNode());
        return container;
    }

    private Node initProjectDetailFragment() {
        final VBox container = new VBox();
        VBox.setVgrow(container, Priority.ALWAYS);
        final ManagedFragmentHandler<ProjectDetailFragment> handler = context.getManagedFragmentHandler(ProjectDetailFragment.class);
        final ProjectDetailFragment controller = handler.getController();
        container.getChildren().addAll(handler.getFragmentNode());
        return container;
    }
}
