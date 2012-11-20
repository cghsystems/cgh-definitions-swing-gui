package net.cghsystems.definitions.client.desktop

import java.awt.BorderLayout as BL
import javax.swing.JOptionPane as OP

import groovy.util.logging.Log4j
import net.cghsystems.definitions.client.DefinitionsClientService
import net.cghsystems.definitions.domain.Definition

import java.awt.Color
import java.awt.FlowLayout
import java.awt.SystemTray
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.annotation.Resource
import javax.swing.BoxLayout
import javax.swing.JFrame
import groovy.swing.SwingBuilder

/**
 * The main container of the Definitions SWING GUI. This GUI renders the main view that displays all of the definitions,
 * and presents the functionality to create, delete, edit and search for definitions.
 *
 *
 * @author Chris
 *
 */
@Log4j
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

    @Resource(name = "desktopClientComponents")
    private DesktopClientComponents desktopClientComponents

    /** The application icon */
    @Resource(name = "iconImage")
    private iconImage

    private resultsList

    private currentSelectedLocation

    void showMain() {
        final title = 'Definitions'
        swingBuilder.edt {
            def frame = frame(title: title, size: [100, 600], show: true, defaultCloseOperation: getDefaultCloseOperation(),
                    pack: true, iconImage: iconImage) {
                lineBorder(color: Color.WHITE, thickness: 10, parent: true)
                lookAndFeel("system")
                resultPanel()
                searchPanel()
                desktopClientComponents.buttonPanel()
            }

            frame.pack()

            def sm = new DefinitionsGUIDisplayStateMachine(component: frame)
            addApplicationTrayIcon(title, frame, sm, shutDownListener)
        }
        searchForNoteAndDisplayResults("")
    }

    void showSorry() {
        log.info("Cannot start the Desktop client at this time as there is no available service")
    }


    private final resultPanel() {
        swingBuilder.scrollPane(constraints: BL.CENTER) {
            resultsList = list(fixedCellWidth: 600, fixedCellHeight: 75, cellRenderer: stripeRender)
        }
    }


    private final searchPanel() {

        final keyListener = [keyTyped: {
            final def handleEscape = {
                searchForNoteAndDisplayResults("")
                it.source.text = ""
            }
            it.keyChar == KeyEvent.VK_ESCAPE ? handleEscape(it) : searchForNoteAndDisplayResults(it.source.text)
        }] as KeyAdapter

        final addButtonGroup = {
            def stores = ["test1", "test2"]

            swingBuilder.panel(background: Color.WHITE, layout: new FlowLayout()) {
                myGroup = buttonGroup()
                stores.each {
                    def r = radioButton(text: it, buttonGroup: myGroup, background: Color.WHITE,
                            actionPerformed: {
                                ds.updateCurrentStoreSource(it.source.text)

                                DefinitionsDesktopClient.log.info("NEED TO IMPLEMENT ds.updateCurrentStoreSource(it.source.text)")

                                searchForNoteAndDisplayResults("")
                            })
                }
            }
        }

        swingBuilder.vbox(constraints: BL.NORTH) {
            addButtonGroup()
            final search = textField()
            search.addKeyListener(keyListener)
            vstrut(height: 10, opaque: true, background: Color.WHITE)
        }
    }

    private def getDefaultCloseOperation() {
        SystemTray.isSupported() ? JFrame.DO_NOTHING_ON_CLOSE : JFrame.EXIT_ON_CLOSE
    }

    /**
     * Hits the Definition service and displays the results
     * @param d
     */
    private void searchForNoteAndDisplayResults(d) {
        swingBuilder.doOutside {
            log.debug("Searching for definition with id: ${d}")
            try {
                final data = definitionsClientService.findDefinition(1)
                log.debug("Found definition ${data} to display")
                //data.sort { it.name.toLowerCase() }
                swingBuilder.edt { resultsList.listData = data }
            } catch (e) {
                log.error("An exception has occured while searching for data", e)
            }
        }
    }

    private void addApplicationTrayIcon(title, frame, sm, shutdownListener) {
        def ti = new DefinitionsTrayIcon(frame: frame, title: title, definitionsGUIDisplayStateMachine: sm, definitionsGUIShutDownListener: shutdownListener)
        ti.addTrayIcon(iconImage)
        shutdownListener << ti
    }
}