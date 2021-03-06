package net.cghsystems.definitions.client.desktop.ioc

import groovy.swing.SwingBuilder
import net.cghsystems.definitions.client.DefinitionsClientService
import net.cghsystems.definitions.client.desktop.DefinitionsDesktopClient
import net.cghsystems.definitions.client.desktop.DefintionsGUIShutdownListener
import net.cghsystems.definitions.client.desktop.StripeRenderer
import net.cghsystems.definitions.client.desktop.componentes.ButtonPanel
import net.cghsystems.definitions.client.desktop.componentes.ResultPanel
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.ImportResource
import org.springframework.context.annotation.Scope
import net.cghsystems.definitions.client.desktop.componentes.SearchPanel

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
    @Scope("prototype")
    def iconImage(swingBuilder) {
        swingBuilder.imageIcon(resource: '/document_text.png').getImage()
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
    def buttonPanel() {
        new ButtonPanel()
    }

    @Bean
    def stripeRenderer() {
        new StripeRenderer()
    }

    @Bean
    def resultsPanel() {
        new ResultPanel()
    }

    @Bean
    def searchPanel()   {
        new SearchPanel()
    }
}
