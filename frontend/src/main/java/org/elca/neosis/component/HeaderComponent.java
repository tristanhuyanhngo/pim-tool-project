package org.elca.neosis.component;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import org.elca.neosis.config.JacpFXConfig;
import org.elca.neosis.factory.ObservableResourceFactory;
import org.jacpfx.api.annotations.component.DeclarativeView;
import org.jacpfx.api.message.Message;
import org.jacpfx.rcp.component.FXComponent;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

@DeclarativeView(
        name = HeaderComponent.ID,
        id = HeaderComponent.ID,
        viewLocation = "/fxml/HeaderComponent.fxml",
        initialTargetLayoutId = JacpFXConfig.HEADER_CONTAINER,
        resourceBundleLocation = ObservableResourceFactory.RESOURCE_BUNDLE_NAME
    )

public class HeaderComponent implements FXComponent {
    public static final String ID = "HeaderComponent";
    public static final String LANGUAGE_OPTION_ACTIVE = "header-language-option-active";

//    @Autowired
//    private ObservableResourceFactory observableResourceFactory;
    @FXML
    private Label labelFR;

    @FXML
    private Label labelEN;

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
//        labelEN.setOnMouseClicked(event -> {
//            observableResourceFactory.switchResourceByLanguage(ObservableResourceFactory.Language.EN);
//            labelFR.getStyleClass().remove(LANGUAGE_OPTION_ACTIVE);
//            if (!labelEN.getStyleClass().contains(LANGUAGE_OPTION_ACTIVE)) {
//                labelEN.getStyleClass().add(LANGUAGE_OPTION_ACTIVE);
//            }
//        });
//        labelFR.setOnMouseClicked(event -> {
//            observableResourceFactory.switchResourceByLanguage(ObservableResourceFactory.Language.FR);
//            labelEN.getStyleClass().remove(LANGUAGE_OPTION_ACTIVE);
//            if (!labelFR.getStyleClass().contains(LANGUAGE_OPTION_ACTIVE)) {
//                labelFR.getStyleClass().add(LANGUAGE_OPTION_ACTIVE);
//            }
//        });
//    }
}
