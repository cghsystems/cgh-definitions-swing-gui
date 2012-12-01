package net.cghsystems.definitions.client.desktop.componentes

import groovy.util.logging.Log4j

/**
 * The Presenter in the MVP pattern representing the searching and display of results
 *
 * @author: chris
 * @date: 28/11/2012
 */
@Log4j
class SearchAndDisplayResultsTrait {

    /**
     * Asks the DefinitionsClientService to search for the definition with the provided id. Updates the resultsPanel with
     * the found definition.
     * <p>
     * <b>NOTE</b> It is the responsibility of the Mixin class to handle the instance of resultsPanel, swingBuilder and
     * definitionsClientService.
     *
     *
     * @param id of the definition to search for
     */
    void searchForDefinitionAndDisplayResults(id) {
        swingBuilder.doOutside {
            log.debug("Searching for definition with id: ${id}")
            try {
                final data = definitionsClientService.findDefinition(1)
                log.debug("Found definition ${data} to display")
                //data.sort { it.name.toLowerCase() }
                notifyResultsPanel([data])
            } catch (e) {
                log.error("An exception has occured while searching for data", e)
            }
        }
    }

    /**
     * Notify the results panel that noting is to be displayed
     */
    void displayEmptyResultsList() {
        notifyResultsPanel([])
    }

    /**
     * @param data for the resultsPanel to render
     */
    private void notifyResultsPanel(data) {
        swingBuilder.edt {
            resultsPanel.notifyOfDataChange(data)
        }
    }

}
