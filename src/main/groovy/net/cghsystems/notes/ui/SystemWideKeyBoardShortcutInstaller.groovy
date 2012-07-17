
package net.cghsystems.notes.ui

import groovy.transform.PackageScope

@PackageScope
class SystemWideKeyBoardShortcutInstaller implements GUIShutdownEvent {

    private definitionsGUIDisplayStateMachine

    private final SHOW_HOT_KEY_ORDINAL = 0

    void install() {
        //        def system = System.getProperty("os.name")
        //        if(system.startsWith("Windows")) {
        //            JIntellitype.instance.registerHotKey(SHOW_HOT_KEY_ORDINAL, JIntellitype.MOD_WIN, (int)'N')
        //            def listener = [onHotKey:{  definitionsGUIDisplayStateMachine.updateState() }] as HotkeyListener
        //            JIntellitype.getInstance().addHotKeyListener(listener)
        //        }
    }

    @Override
    void onClose() {
        //        println "Closing Shortcuts"
        //        JIntellitype.instance.unregisterHotKey(SHOW_HOT_KEY_ORDINAL);
    }
}
