package net.cghsystems.definitions.client.desktop


import java.awt.BorderLayout as BL

import groovy.swing.SwingBuilder
import groovy.util.logging.Log4j
import net.cghsystems.definitions.client.DefinitionsClientService
import net.cghsystems.definitions.client.desktop.componentes.ButtonPanel

import java.awt.Color
import java.awt.FlowLayout
import java.awt.SystemTray
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.annotation.Resource
import javax.swing.JFrame
import net.cghsystems.definitions.client.desktop.componentes.SearchAndDisplayResultsTrait
import net.cghsystems.definitions.client.desktop.componentes.SearchPanel

/**
 * The main container of the Definitions SWING GUI. This GUI renders the main view that displays all of the definitions,
 * and presents the functionality to create, delete, edit and search for definitions.
 *
 *
 * @author Chris
 *
 */
@Log4j
@Mixin(SearchAndDisplayResultsTrait)
class DefinitionsDesktopClient {

    @Resource(name = "swingBuilder")
    private SwingBuilder swingBuilder

    /** To notify listeners that the application is closing to allow a graceful shutdown */
    @Resource(name = "shutdownListener")
    private shutDownListener

    /** Allows each row to be displayed with an alternative colour to improve highlighting */
    @Resource(name = "stripeRenderer")
    private final stripeRender

    /** To provide connectivity to the Definitions services */
    @Resource(name = "definitionsClientService")
    private DefinitionsClientService definitionsClientService;

    @Resource(name = "buttonPanel")
    private ButtonPanel buttonPanel

    @Resource(name = "searchPanel")
    private SearchPanel searchPanel

    /** The application icon */
    @Resource(name = "iconImage")
    private iconImage

    @Resource(name = "shutdownListener")
    private shutdownListener

    @Resource(name = "resultsPanel")
    private resultsPanel


    private currentSelectedLocation

    //TODO Ioc @Value
    private title = "title"


    void showMain() {
        swingBuilder.edt {
            def frame = frame(title: title, size: [100, 600], show: true, defaultCloseOperation: getDefaultCloseOperation(),
                    pack: true, iconImage: iconImage) {
                lineBorder(color: Color.WHITE, thickness: 10, parent: true)
                lookAndFeel("system")
                resultsPanel.resultPanel()
                searchPanel.searchPanel()
                buttonPanel.buttonPanel()
            }

            def sm = new DefinitionsGUIDisplayStateMachine(component: frame)
            addApplicationTrayIcon(title, frame, sm)
        }
        displayEmptyResultsList()
    }

    void showSorry() {
        log.info("Cannot start the Desktop client at this time as there is no available service")
    }

    private def getDefaultCloseOperation() {
        SystemTray.isSupported() ? JFrame.DO_NOTHING_ON_CLOSE : JFrame.EXIT_ON_CLOSE
    }

    private void addApplicationTrayIcon(def title, def frame, def sm) {
        def ti = new DefinitionsTrayIcon(frame: frame, title: title, definitionsGUIDisplayStateMachine: sm, definitionsGUIShutDownListener: shutdownListener)
        ti.addTrayIcon(iconImage)
        shutdownListener << ti
    }
}