package net.cghsystems.definitions.client.desktop.componentes

import groovy.swing.SwingBuilder
import groovy.util.logging.Log4j

import java.awt.BorderLayout
import javax.annotation.Resource

/**
 * @author: chris
 * @date: 15/11/2012
 */
@Log4j
class DesktopClientComponents {

    @Resource(name = "buttonPanel")
    ButtonPanel buttonPanel


    def buttonPanel() {
        buttonPanel.buttonPanel()
    }
}
