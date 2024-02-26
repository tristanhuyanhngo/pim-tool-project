package org.elca.neosis.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"org.elca.neosis"})
public class JacpFXConfig {
    public static final String HEADER_CONTAINER = "HeaderContainer";
    public static final String MENU_CONTAINER = "MenuContainer";
    public static final String MAIN_CONTENT_CONTAINER = "MainContentContainer";
}
