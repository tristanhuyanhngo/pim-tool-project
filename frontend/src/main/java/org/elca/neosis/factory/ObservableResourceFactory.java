package org.elca.neosis.factory;

import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.Locale;
import java.util.ResourceBundle;

/*
 * References: https://stackoverflow.com/questions/32464974/javafx-change-application-language-on-the-run
 */
public class ObservableResourceFactory {
    public final static String RESOURCE_BUNDLE_NAME = "bundles/languageBundle";

    private ObjectProperty<ResourceBundle> resources = new SimpleObjectProperty<>();

    public ObjectProperty<ResourceBundle> resourcesProperty() {
        return resources;
    }

    public final ResourceBundle getResources() {
        return resourcesProperty().get();
    }

    public StringBinding getStringBinding(String key) {
        return new StringBinding() {
            { bind(resourcesProperty()); }
            @Override
            public String computeValue() {
                return getResources().getString(key);
            }
        };
    }

    public enum Language {
        FR(Locale.FRENCH), EN(Locale.ENGLISH);
        private Locale locale;

        Language(Locale locale) {
            this.locale = locale;
        }

        public Locale getLocale() {
            return locale;
        }
    }

    public final void switchResourceByLanguage(Language language) {
        resourcesProperty().set(ResourceBundle.getBundle(RESOURCE_BUNDLE_NAME, language.getLocale()));
    }
}
