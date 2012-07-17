/**
 * 
 */
package net.cghsystems.notes.ui

import groovy.lang.PackageScope

@PackageScope
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
