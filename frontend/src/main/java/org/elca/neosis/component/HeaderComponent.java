package org.elca.neosis.component;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import org.elca.neosis.common.ApplicationBundleKey;
import org.elca.neosis.config.JacpFXConfig;
import org.elca.neosis.multilingual.I18N;
import org.jacpfx.api.annotations.component.DeclarativeView;
import org.jacpfx.api.message.Message;
import org.jacpfx.rcp.component.FXComponent;

@DeclarativeView(
        name = HeaderComponent.ID,
        id = HeaderComponent.ID,
        viewLocation = "/fxml/HeaderComponent.fxml",
        resourceBundleLocation = I18N.BUNDLE_NAME,
        initialTargetLayoutId = JacpFXConfig.HEADER_CONTAINER
    )

public class HeaderComponent implements FXComponent {
    public static final String ID = "HeaderComponent";
    public static final String LANGUAGE_OPTION_ACTIVE = "header-language-option-active";

    @FXML
    private Label labelFR;

    @FXML
    private Label labelEN;

    @FXML
    private Label labelAppTitle;

    @Override
    public Node postHandle(Node node, Message<Event, Object> message) throws Exception {
        labelEN.setOnMouseClicked(event -> {
            I18N.setLocale(I18N.Language.EN.getLocale());
            labelFR.getStyleClass().remove(LANGUAGE_OPTION_ACTIVE);
            if (!labelEN.getStyleClass().contains(LANGUAGE_OPTION_ACTIVE)) {
                labelEN.getStyleClass().add(LANGUAGE_OPTION_ACTIVE);
            }
        });
        labelFR.setOnMouseClicked(event -> {
            I18N.setLocale(I18N.Language.FR.getLocale());
            labelEN.getStyleClass().remove(LANGUAGE_OPTION_ACTIVE);
            if (!labelFR.getStyleClass().contains(LANGUAGE_OPTION_ACTIVE)) {
                labelFR.getStyleClass().add(LANGUAGE_OPTION_ACTIVE);
            }
        });

        initMultilingual();
        return null;
    }
    @Override
    public Node handle(Message<Event, Object> message) throws Exception {
        return null;
    }

    private void initMultilingual() {
        labelAppTitle.textProperty().bind(I18N.createStringBinding(ApplicationBundleKey.LABEL_APP_TITLE));
    }
}

// Create Pull Request
