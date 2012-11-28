package net.cghsystems.definitions.client.desktop.componentes

/**
 * @author: chris
 * @date: 28/11/2012
 */
class SearchAndDisplayResultsTrait {

    /**
     * Asks the DefinitionsClientService to search for the definition with the provided id. Updates the resultsPanel with
     * the found definition.
     * <p>
     * <b>NOTE</b> It is the responsibility of the Mixin class to handle the instance of resultsPanel, swingBuilder and
     * definitionsClientService.
     *
     *
     * @param id
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

    void displayEmptyResultsList() {
        log.info("Notifying results panel of empty results")
        notifyResultsPanel([])
    }

    private void notifyResultsPanel(data) {
        swingBuilder.edt {
            resultsPanel.notifyOfDataChange(data)
        }
    }

}
