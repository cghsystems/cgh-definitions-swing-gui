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
    private swingBuilder

    /** To notify listeners that the application is closing to allow a graceful shutdown */
    @Resource(name = "shutdownListener")
    private shutDownListener

    /** Allows each row to be displayed with an alternative colour to improve highlighting */
    @Resource(name = "stripRenderer")
    private final stripeRender

    /** To provide connectivity to the Definitions services */
    @Resource(name = "definitionsClientService")
    private DefinitionsClientService definitionsClientService;

    /** The application icon */
    @Resource(name = "iconImage")
    private iconImage

    private resultsList

    private currentSelectedLocation

    /**
     * The action that is executed on the 'delete' event. This is triggered on the click of the 'Delete' button
     * contained in the button pannel
     */
    private final deleteNoteDialogAction = {

        final toDelete = stripeRender.currentlySelected?.id ?: ""
        def optionPane = swingBuilder.optionPane()

        def nothingToDelete = {
            optionPane.showMessageDialog(null, "Please select a Note to delete", "Nothing to Delete", OP.PLAIN_MESSAGE)
        }

        def delete = {
            def options = ["Yes", "No"] as Object[]
            def choice = optionPane.showOptionDialog(null, "Do you want to delete ${stripeRender.currentlySelected.name}", 'Delete Note',
                    OP.YES_NO_OPTION, OP.QUESTION_MESSAGE, null, options, "")

            if (options[choice] == "Yes") {
                log.info("Will delete definition with id: ${toDelete}")
                definitionsClientService.deleteDefinition(toDelete as Integer)
                searchForNoteAndDisplayResults("")
            }
        }

        toDelete == "" ? nothingToDelete() : delete()
    }

    void show() {
        final title = 'Definitions'
        swingBuilder.edt {
            def frame = frame(title: title, size: [100, 600], show: true, defaultCloseOperation: getDefaultCloseOperation(),
                    pack: true, iconImage: iconImage) {
                lineBorder(color: Color.WHITE, thickness: 10, parent: true)
                lookAndFeel("system")
                resultPanel()
                searchPanel()
                buttonPanel()
            }
            def sm = new DefinitionsGUIDisplayStateMachine(component: frame)
            addApplicationTrayIcon(title, frame, sm, shutDownListener)
        }
        searchForNoteAndDisplayResults("")
    }

    //TODO inverse
    final addOrEditNoteDialog(title, currentlySelected, addOrEditNote) {
        swingBuilder.dialog(id: 'addOrEditDialog', modal: true, title: "${title} Note", size: [400, 220]) {
            panel(background: Color.WHITE, opaque: true) {
                boxLayout(axis: BoxLayout.Y_AXIS)
                lineBorder(color: Color.WHITE, thickness: 10, parent: true)
                label("Name:", name: "name-label")
                final name = textField(name: "name-textfield", currentlySelected?.name ?: "")

                vstrut(height: 10, opaque: true, background: Color.WHITE)
                label("Definition", name: "definition-label")
                final definition = textField(name: "definition-textfield", currentlySelected?.definition ?: "")

                vstrut(height: 10, opaque: true, background: Color.WHITE)
                label("Description", name: "description-label")
                final description = textField(name: "description-textfield", currentlySelected?.description ?: "")

                vstrut(height: 10, opaque: true, background: Color.WHITE)


                button(name: "addedit-button", text: "${title}", actionPerformed: {
                    addOrEditNote(currentlySelected?.id, name.text, definition.text, description.text)
                    addOrEditDialog.dispose()
                })
            }
        }
    }

    private final resultPanel() {
        swingBuilder.scrollPane(constraints: BL.CENTER) {
            resultsList = list(fixedCellWidth: 600, fixedCellHeight: 75, cellRenderer: stripeRender)
        }
    }

    //TODO Inverse
    public final buttonPanel() {
        swingBuilder.panel(constraints: BL.SOUTH, opaque: true, background: Color.WHITE) {
            flowLayout()

            button("Add", mnemonic: "A", name: "add-definition", actionPerformed: {
                addOrEditNoteDialog("Add", null, {id, name, definitiion, description ->
                    definitionsClientService.createDefinition(new Definition(id: 1, name: name,
                            definition: definitiion,
                            description: description,
                            definitionCategoryId: 1))
                    searchForNoteAndDisplayResults(name)
                }).show()
            })

            button("Delete", mnemonic: "D", name: "delete-definition", actionPerformed: deleteNoteDialogAction)

            button("Edit", mnemonic: "E", name: "edit-definition", actionPerformed: {
                addOrEditNoteDialog("Edit", stripeRender.currentlySelected, {id, name, definitiion, description ->
                    ds.edit(id, name, definitiion, description)
                    searchForNoteAndDisplayResults(name)
                }).show()
            })

            button("Close", mnemonic: "C", name: "close", actionPerformed: { shutDownListener.notifyOnClose() })
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
        //Autowire this dude
        def ti = new DefinitionsTrayIcon(frame: frame, title: title, definitionsGUIDisplayStateMachine: sm, definitionsGUIShutDownListener: shutdownListener)
        ti.addTrayIcon()
        shutdownListener << ti
    }
}