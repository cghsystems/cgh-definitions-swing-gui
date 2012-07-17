package net.cghsystems.notes.ui


import groovy.lang.PackageScope
import groovy.swing.SwingBuilder

import java.awt.Frame
import java.awt.MenuItem
import java.awt.PopupMenu
import java.awt.SystemTray
import java.awt.TrayIcon
import java.awt.event.ActionListener
import java.awt.event.WindowAdapter

@PackageScope
class DefinitionsTrayIcon implements GUIShutdownEvent {

    private definitionsGUIDisplayStateMachine
    private definitionsGUIShutDownListener
    private title
    private trayIcon
    private frame

    private static final IS_MAXIMISED = 1;
    private static final IS_MINIMISED = 0;


    private final actionListener =  {action ->
        [ actionPerformed: {  action() } ] as ActionListener
    }

    private final showHide = { definitionsGUIDisplayStateMachine.updateState() }

    void addTrayIcon() {
        if (SystemTray.isSupported()) {

            addWindowListenersToFrame(frame)

            def icon = getIconImage()
            trayIcon = new TrayIcon(icon, title)
            trayIcon.setPopupMenu(getPopupMenu())

            trayIcon.addActionListener(actionListener( { showHide() } ))
            SystemTray.getSystemTray().add(trayIcon)
        }
    }

    private void addWindowListenersToFrame(frame) {
        def swingBuilder = new SwingBuilder()
        frame.addWindowListener(
                [windowClosing:{
                        swingBuilder.edt { frame.setVisible(false) }
                    },
                    windowIconified:{
                        swingBuilder.edt {
                            int state = frame.getExtendedState()
                            state = state | Frame.ICONIFIED
                            frame.setExtendedState(state)
                            frame.setVisible(false)
                        }
                    }
                ] as WindowAdapter )
    }

    private def getIconImage() {
        new SwingBuilder().imageIcon(resource:'/document_text.png').getImage()
    }

    private def getPopupMenu() {
        PopupMenu popup = new PopupMenu();
        addShowMenuItemToPopup(popup)
        addExitMenuItemToPopup(popup)
        popup
    }

    private void addShowMenuItemToPopup(popup) {
        def exit = new MenuItem("Show");
        exit.addActionListener(actionListener({ showHide() }))
        popup.add(exit)
    }

    private void addExitMenuItemToPopup(popup) {
        def exit = new MenuItem("Exit");
        exit.addActionListener(actionListener({ definitionsGUIShutDownListener.notifyOnClose() }))
        popup.add(exit)
    }

    @Override
    void onClose() {
        println "Closing TrayIcon"
        SystemTray.getSystemTray().remove(trayIcon)
    }
}
