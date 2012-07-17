package net.cghsystems.notes.ui

import java.awt.Frame

@PackageScope
class DefinitionsGUIDisplayStateMachine {

    private component

    void updateState() {
        final isVisible = component.isVisible()
        final isMinimised = component.getExtendedState()

        updateVisibility(isVisible)
        updateExtendedState(isMinimised)
        updateFocus()
    }

    private void updateExtendedState(isMinimised) {
        !isMinimised ?: component.setExtendedState(Frame.NORMAL)
    }

    private void updateVisibility(isVisible) {
        isVisible ?: component.setVisible(true)
    }

    private void updateFocus() {
        component.show()
    }
}
