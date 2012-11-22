package net.cghsystems.definitions.client.desktop.componentes

import groovy.util.logging.Log4j
import net.cghsystems.definitions.domain.Definition

import java.awt.BorderLayout
import java.awt.Color
import javax.annotation.Resource
import javax.swing.BoxLayout
import javax.swing.JOptionPane

/**
 * @author: chris
 * @date: 21/11/2012
 */
@Log4j
class ButtonPanel {

    @Resource(name = "shutdownListener")
    def shutdownListener

    @Resource(name = "swingBuilder")
    def swingBuilder

    @Resource(name = "definitionsClientService")
    def definitionsClientService

    @Resource(name = "stripeRenderer")
    def stripeRenderer

    @Resource(name = "resultsPanel")
    def resultsPanel

    final buttonPanel() {
        swingBuilder.panel(constraints: BorderLayout.SOUTH, opaque: true, background: Color.WHITE) {
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
                addOrEditNoteDialog("Edit", stripeRenderer.currentlySelected, {id, name, definitiion, description ->
                    ds.edit(id, name, definitiion, description)
                    searchForNoteAndDisplayResults(name)
                }).show()
            })

            button("Close", mnemonic: "C", name: "close", actionPerformed: {
                shutdownListener.notifyOnClose({System.exit(0)})
            })
        }
    }

    void searchForNoteAndDisplayResults(id) {
        swingBuilder.doOutside {
            log.debug("Searching for definition with id: ${id}")
            try {
                final data = definitionsClientService.findDefinition(1)
                log.debug("Found definition ${data} to display")
                //data.sort { it.name.toLowerCase() }
                swingBuilder.edt {
                    //This should be a listener pattern to loose the coupling
                    resultsPanel.notifyOfDataChange([data])
                }
            } catch (e) {
                log.error("An exception has occured while searching for data", e)
            }
        }
    }


    def addOrEditNoteDialog(title, currentlySelected, addOrEditNote) {
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

    /**
     * The action that is executed on the 'delete' event. This is triggered on the click of the 'Delete' button
     * contained in the button panel
     */
    private final deleteNoteDialogAction = {

        final toDelete = stripeRender.currentlySelected?.id ?: ""
        def optionPane = swingBuilder.optionPane()

        def nothingToDelete = {
            optionPane.showMessageDialog(null, "Please select a Note to delete", "Nothing to Delete", JOptionPane.PLAIN_MESSAGE)
        }

        def delete = {
            def options = ["Yes", "No"] as Object[]
            def choice = optionPane.showOptionDialog(null, "Do you want to delete ${stripeRender.currentlySelected.name}", 'Delete Note',
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, "")

            if (options[choice] == "Yes") {
                log.info("Will delete definition with id: ${toDelete}")
                definitionsClientService.deleteDefinition(toDelete as Integer)
                searchForNoteAndDisplayResults("")
            }
        }

        toDelete == "" ? nothingToDelete() : delete()
    }
}