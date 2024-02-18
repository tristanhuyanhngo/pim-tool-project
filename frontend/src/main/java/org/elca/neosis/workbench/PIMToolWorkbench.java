package org.elca.neosis.workbench;

import javafx.event.Event;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.elca.neosis.perspective.MainPerspective;
import org.jacpfx.api.annotations.workbench.Workbench;
import org.jacpfx.api.componentLayout.WorkbenchLayout;
import org.jacpfx.api.message.Message;
import org.jacpfx.api.util.ToolbarPosition;
import org.jacpfx.rcp.componentLayout.FXComponentLayout;
import org.jacpfx.rcp.workbench.FXWorkbench;

// The workbench is the UI-root node of a JacpFX application.
@Workbench(id = PIMToolWorkbench.ID, name = PIMToolWorkbench.ID, perspectives = { MainPerspective.ID})
public class PIMToolWorkbench implements FXWorkbench {
    public static final String ID = "PIMToolWorkbench";

    @Override
    public void postHandle(FXComponentLayout layout) {
        // Noncompliance - method is empty
    }

    @Override
    public void handleInitialLayout(final Message<Event, Object> action, final WorkbenchLayout<Node> layout, final Stage stage) {
        layout.setWorkbenchXYSize(1024, 768);
        layout.registerToolBar(ToolbarPosition.NORTH);
        layout.setStyle(StageStyle.DECORATED);
        layout.setMenuEnabled(false);
    }
}
