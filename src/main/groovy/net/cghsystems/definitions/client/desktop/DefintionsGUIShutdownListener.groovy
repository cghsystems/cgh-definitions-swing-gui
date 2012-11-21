/**
 * 
 */
package net.cghsystems.definitions.client.desktop

import groovy.util.logging.Log4j

/**
 * Listens for a shutdown event so that all listeners can be notified to enable a graceful shutdown.
 */
@Log4j
class DefintionsGUIShutdownListener {

    private final listeners = []

    def add(GUIShutdownEvent event) {
        listeners << event
    }

    def leftShift(GUIShutdownEvent event) {
        add(event)
    }

    /**
     * Notifies all registered listeners that the application is about to shutdown.
     */
    def notifyOnClose() {
        listeners.each { it.onClose() }
        log.info("Closing Definitions Swing Client")
        System.exit(0)
    }
}
