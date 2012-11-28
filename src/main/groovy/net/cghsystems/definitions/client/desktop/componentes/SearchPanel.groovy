package net.cghsystems.definitions.client.desktop.componentes

import java.awt.BorderLayout
import java.awt.Color
import java.awt.FlowLayout
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.annotation.Resource
import groovy.swing.SwingBuilder
import net.cghsystems.definitions.client.DefinitionsClientService
import groovy.util.logging.Log4j

/**
 * @author: chris
 * @date: 28/11/2012
 */
@Mixin(SearchAndDisplayResultsTrait)
@Log4j
class SearchPanel {

    @Resource(name = "swingBuilder")
    SwingBuilder swingBuilder

    @Resource(name = "definitionsClientService")
    DefinitionsClientService definitionsClientService

    @Resource
    ResultPanel resultsPanel

    def searchPanel() {

        /**
         * Key listener for the search panel. Allows the results panel to be updated with results aligned with the
         * contents of the search textfield.
         */
        final keyListener = [keyPressed: {
            final def handleEscape = {
                log.info("Handling Escape")
                displayEmptyResultsList()
                it.source.text = ""
            }

            log.info("Handing search panel key event")
            it.keyChar == KeyEvent.VK_ESCAPE ? handleEscape(it) : searchForDefinitionAndDisplayResults(it.source.text)
        }] as KeyAdapter

        /**
         * Button group to allow users to limit results displayed in a resultsPanel to a given category.
         */
        final addButtonGroup = {

            def stores = definitionsClientService.findAllCategories()

            swingBuilder.panel(background: Color.WHITE, layout: new FlowLayout()) {
                def categoryButtonGroup = buttonGroup()
                stores.each {
                    radioButton(name: "${it}-radionbutton", text: it, buttonGroup: categoryButtonGroup, background: Color.WHITE,
                            actionPerformed: {
                                ds.updateCurrentStoreSource(it.source.text)
                                searchForDefinitionAndDisplayResults("")
                            })
                }
            }
        }

        /**
         * Build the component to add search functionality.
         */
        swingBuilder.vbox(constraints: BorderLayout.NORTH) {
            addButtonGroup()
            final search = textField(name: "search-textfield")
            search.addKeyListener(keyListener)
            vstrut(height: 10, opaque: true, background: Color.WHITE)
        }
    }
}
