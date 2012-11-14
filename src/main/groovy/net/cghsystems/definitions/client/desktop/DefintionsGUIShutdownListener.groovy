/**
 * 
 */
package net.cghsystems.definitions.client.desktop


class DefintionsGUIShutdownListener {

    private final listeners = []

    def add(GUIShutdownEvent event) {
        listeners << event
    }

    def leftShift(GUIShutdownEvent event) {
        add(event)
    }

    def notifyOnClose() {
        listeners.each { it.onClose() }
        System.exit(0)
    }
}
