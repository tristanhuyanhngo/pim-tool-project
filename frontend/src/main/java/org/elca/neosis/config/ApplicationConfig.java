package org.elca.neosis.config;

import org.elca.neosis.factory.ObservableResourceFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"org.elca.neosis"})
public class ApplicationConfig {
    @Bean
    public ObservableResourceFactory observableResourceFactory() {
        ObservableResourceFactory observableResourceFactory = new ObservableResourceFactory();
        observableResourceFactory.switchResourceByLanguage(ObservableResourceFactory.Language.EN);
        return observableResourceFactory;
    }
}
