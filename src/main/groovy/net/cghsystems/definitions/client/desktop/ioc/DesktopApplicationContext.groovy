package net.cghsystems.definitions.client.desktop.ioc

import net.cghsystems.definitions.client.DefinitionsClientService
import net.cghsystems.definitions.client.desktop.DefinitionsDesktopClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.ImportResource
import groovy.swing.SwingBuilder
import net.cghsystems.definitions.client.desktop.DefintionsGUIShutdownListener
import net.cghsystems.definitions.client.desktop.StripeRenderer

/**
 * @author: chris
 * @date: 14/11/2012
 */
@Configuration
@ImportResource("classpath:META-INF/spring/definitions-services-client-si-context.xml")
class DesktopApplicationContext {

    @Bean
    def definitionsDesktopClient() {
        new DefinitionsDesktopClient()
    }

    @Bean
    def definitionsClientService() {
        new DefinitionsClientService()
    }

    @Bean
    def iconImage() {
        new SwingBuilder().imageIcon(resource:'/document_text.png').getImage()
    }

    @Bean
    def shutdownListener() {
        new DefintionsGUIShutdownListener()
    }

    @Bean
    def swingBuilder() {
        new SwingBuilder()
    }

    @Bean
    def stripRenderer(swingBuilder) {
        new StripeRenderer(builder:swingBuilder)
    }
}
