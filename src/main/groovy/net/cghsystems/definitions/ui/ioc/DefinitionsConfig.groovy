package net.cghsystems.definitions.ui.ioc

import groovy.swing.SwingBuilder
import net.cghsystems.definitions.rest.client.DefinitionsRestEndpoint
import net.cghsystems.definitions.ui.DefinitionsGUI
import net.cghsystems.definitions.ui.DefintionsGUIShutdownListener
import net.cghsystems.definitions.ui.StripeRenderer

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.ImportResource
import org.springframework.context.annotation.Lazy


@Configuration
@ImportResource("classpath:META-INF/spring/definitions-services-client-si-context.xml")
class DefinitionsConfig {

    @Bean
    @Lazy
    DefinitionsGUI definitionsGUI() {
        new DefinitionsGUI()
    }

    @Bean
    @Lazy
    SwingBuilder swingBuilder() {
        new SwingBuilder()
    }

    @Bean
    @Lazy
    StripeRenderer stripeRenderer() {
        new StripeRenderer()
    }

    @Bean
    @Lazy
    DefintionsGUIShutdownListener shutdownListener() {
        new DefintionsGUIShutdownListener()
    }

    @Bean()
    @Lazy
    DefinitionsRestEndpoint definitionsRestEndpoint() {
        new DefinitionsRestEndpoint()
    }
}
