package org.elca.neosis.main;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.elca.neosis.config.ApplicationConfig;
import org.jacpfx.rcp.workbench.FXWorkbench;
import org.jacpfx.spring.launcher.AFXSpringJavaConfigLauncher;
import org.elca.neosis.workbench.PIMToolWorkbench;

// The application – launcher is the main class of your application where you define the Spring context.
// The leaf nodes are the “execution-target” (container) for all components associated (injected) to this perspective.
public class ApplicationLauncher extends AFXSpringJavaConfigLauncher {

    @Override
    protected Class<?>[] getConfigClasses() {
        return new Class<?>[]{ApplicationConfig.class}; // <?> - Wildcard | Unbounded wildcard
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

    public ApplicationLauncher() {}

    @Override
    public void postInit(Stage stage) {
        // This method gives you access to the JavaFX stage. You may define a stylesheet for your application.
        Scene scene = stage.getScene();
    }
}
