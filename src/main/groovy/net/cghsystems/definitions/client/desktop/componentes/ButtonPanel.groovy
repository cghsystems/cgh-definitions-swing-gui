package net.cghsystems.definitions.client.desktop.componentes

import groovy.swing.SwingBuilder
import groovy.util.logging.Log4j
import net.cghsystems.definitions.domain.Definition

import java.awt.BorderLayout
import java.awt.Color
import javax.annotation.Resource
import javax.swing.BoxLayout
import javax.swing.JOptionPane

/**
 * Encapsulates all of the Buttons and their actions that are shown at the foot of the Definitions GUI.
 *
 * @author: chris
 * @date: 21/11/2012
 */
@Log4j
@Mixin(SearchAndDisplayResultsTrait)
class ButtonPanel {

    @Resource(name = "shutdownListener")
    def shutdownListener

    @Resource(name = "swingBuilder")
    SwingBuilder swingBuilder

    @Resource(name = "definitionsClientService")
    def definitionsClientService

    @Resource(name = "stripeRenderer")
    def stripeRenderer

    @Resource(name = "resultsPanel")
    def resultsPanel

    /**
     * Adds a definition to be added or updated. Delegates to definitionsClientService. ONce a definition is added the
     * resultsPanel is updated with the changes.
     */
    def addOrUpdateDefinitionAction = {id, name, definition, description ->

         def created = definitionsClientService.createDefinition(new Definition(name: name,
                definition: definition,
                description: description,
                definitionCategoryId: 1))

        searchForDefinitionAndDisplayResults(created.id)
    }

    /**
     * The action that is executed on the 'delete' event. This is triggered on the click of the 'Delete' button
     * contained in the button panel
     */
    def deleteNoteDialogAction = { frame ->

        final toDelete = stripeRenderer.currentlySelected?.id ?: ""

        def optionPane
        swingBuilder.edt { optionPane = swingBuilder.optionPane() }

        def nothingToDelete = {
            swingBuilder.edt {
                optionPane.showMessageDialog(frame, "Please select a Note to delete", "Nothing to Delete", JOptionPane.PLAIN_MESSAGE)
            }
        }

        def delete = {
            def options = ["Yes", "No"] as Object[]
            def choice = optionPane.showOptionDialog(null, "Do you want to delete ${stripeRenderer.currentlySelected.name}", 'Delete Note',
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, "")

            if (options[choice] == "Yes") {
                log.info("Will delete definition with id: ${toDelete}")
                definitionsClientService.deleteDefinition(toDelete)
                displayEmptyResultsList()
            }
        }

        toDelete == "" ? nothingToDelete() : delete()
    }

    /**
     * The components that encapsulates the add, edit, delete and close buttons
     * @return
     */
    final buttonPanel() {
        swingBuilder.panel(constraints: BorderLayout.SOUTH, opaque: true, background: Color.WHITE) {
            flowLayout()

            button("Add", mnemonic: "A", name: "add-definition", actionPerformed: {
                addOrEditDefinitionDialog("Add", null, addOrUpdateDefinitionAction).show()
            })

            button("Delete", mnemonic: "D", name: "delete-definition", actionPerformed: deleteNoteDialogAction)

            button("Edit", mnemonic: "E", name: "edit-definition", actionPerformed: {
                addOrEditDefinitionDialog("Edit", stripeRenderer.currentlySelected, addOrUpdateDefinitionAction).show()
            })

            button("Close", mnemonic: "C", name: "close", actionPerformed: {
                shutdownListener.notifyOnClose({System.exit(0)})
            })
        }
    }



    /**
     * Dialog that allows a user to enter new Definition data. If an edit action is requested then the existing data is
     * displayed to allow for an edit to take place.
     *
     * @param title
     * @param currentlySelected
     * @param addOrEditNote
     * @return
     */
    def addOrEditDefinitionDialog(title, currentlySelected, addOrEditNote) {
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

}
