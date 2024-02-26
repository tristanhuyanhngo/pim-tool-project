package org.elca.neosis;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.elca.neosis.config.JacpFXConfig;
import org.elca.neosis.workbench.PIMToolWorkbench;
import org.jacpfx.rcp.workbench.FXWorkbench;
import org.jacpfx.spring.launcher.AFXSpringJavaConfigLauncher;

import java.util.Objects;

// The application – launcher is the main class of your application where you define the Spring context.
// The leaf nodes are the “execution-target” (container) for all components associated (injected) to this perspective.
public class PIMToolFrontendApplication extends AFXSpringJavaConfigLauncher {
    private static final String APP_LOGO_PATH = "/images/logo_elca.png";

    @Override
    protected Class<?>[] getConfigClasses() {
        return new Class<?>[]{JacpFXConfig.class}; // <?> - Wildcard | Unbounded wildcard
        // returns an array with all valid spring configuration classes (annotated with @Configuration)
    }

    @Override
    protected Class<? extends FXWorkbench> getWorkbenchClass() {
        return PIMToolWorkbench.class; // Returns the defined Workbench class.
    }

    @Override
    protected String[] getBasePackages() {
        return new String[]{"org.elca.neosis"};
        // Define all packages to scan for Components and Perspectives.
        // JacpFX uses component scanning to resolve all Components and Perspectives by ID.
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

    public PIMToolFrontendApplication() {}

    @Override
    public void postInit(Stage stage) {
        // This method gives you access to the JavaFX stage. You may define a stylesheet for your application.
        Scene scene = stage.getScene();
        stage.getIcons().add(new Image(
                Objects.requireNonNull(getClass().getResourceAsStream(APP_LOGO_PATH), "Missing ELCA Logo")));
        stage.setTitle("PIM Tool");
    }
}
