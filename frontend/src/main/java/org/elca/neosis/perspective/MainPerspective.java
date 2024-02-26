package org.elca.neosis.perspective;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.elca.neosis.component.HeaderComponent;
import org.elca.neosis.component.MainContentComponent;
import org.elca.neosis.component.MenuComponent;
import org.elca.neosis.config.JacpFXConfig;
import org.jacpfx.api.annotations.lifecycle.PostConstruct;
import org.jacpfx.api.annotations.perspective.Perspective;
import org.jacpfx.api.message.Message;
import org.jacpfx.rcp.componentLayout.FXComponentLayout;
import org.jacpfx.rcp.componentLayout.PerspectiveLayout;
import org.jacpfx.rcp.perspective.FXPerspective;

import java.util.ResourceBundle;

// The task of a perspective is to provide the layout of the current view and to register the root and all leaf nodes of the view
@Perspective(
        name = MainPerspective.ID,
        id = MainPerspective.ID,
        viewLocation = "/fxml/MainPerspective.fxml",
        components = {
                HeaderComponent.ID,
                MenuComponent.ID,
                MainContentComponent.ID
        }
)
public class MainPerspective implements FXPerspective {
    public static final String ID = "MainPerspective";
    @FXML
    private VBox headerContainer;

    @FXML
    private HBox menuContainer;

    @FXML
    private VBox mainContentContainer;

    @Override
    public void handlePerspective(Message<Event, Object> message, PerspectiveLayout perspectiveLayout) {
        // Noncompliance - method is empty
    }

    @PostConstruct
    public void onStartPerspective(final PerspectiveLayout perspectiveLayout, final FXComponentLayout layout,
                                   final ResourceBundle resourceBundle) {
        perspectiveLayout.registerTargetLayoutComponent(JacpFXConfig.HEADER_CONTAINER, headerContainer);
        perspectiveLayout.registerTargetLayoutComponent(JacpFXConfig.MENU_CONTAINER, menuContainer);
        perspectiveLayout.registerTargetLayoutComponent(JacpFXConfig.MAIN_CONTENT_CONTAINER, mainContentContainer);
    }
}
