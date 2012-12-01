package net.cghsystems.definitions.client.desktop.componentes

import groovy.swing.SwingBuilder
import groovy.util.logging.Log4j
import net.cghsystems.definitions.client.DefinitionsClientService
import org.fest.swing.edt.GuiActionRunner
import org.fest.swing.edt.GuiQuery
import org.fest.swing.fixture.FrameFixture
import spock.lang.Specification

import java.awt.event.KeyEvent
import javax.swing.JFrame

/**
 * @author: chris
 * @date: 28/11/2012
 */
@Log4j
class SearchPanelTest extends Specification {

    private SearchPanel unit;

    void setup() {
        unit = new SearchPanel(swingBuilder: new SwingBuilder())
    }

    def "should have the expected components"() {

        given: "the unit has a defintionsClientService"
        final definitionsClientService = Mock(DefinitionsClientService)
        unit.definitionsClientService = definitionsClientService

        when: "a search panel is visible"
        FrameFixture f = GuiActionRunner.execute([executeInEDT: {
            final d = unit.searchPanel()
            final f = new JFrame()
            f.add(d)
            new FrameFixture(f)
        }] as GuiQuery)

        f.show()

        then: "defintionsClientService should have been called"
        1 * definitionsClientService.findAllCategories() >> ["Category-1", "Category-2"]

        and: "It should have an empty search text field"
        f.textBox("search-textfield").requireEmpty()

        and: "A button group showing the categroies"
        f.radioButton("Category-1-radionbutton").requireVisible()
        f.radioButton("Category-1-radionbutton").requireVisible()

        and: "Cleaup"
        f.cleanUp()
    }

    def "When search field has text and escape is pushed then text should be deleted"() {

        given: "the unit has a defintionsClientService"
        final definitionsClientService = Mock(DefinitionsClientService)
        unit.definitionsClientService = definitionsClientService

        and: "the unit has a results panel"
        def resultPanel = Mock(ResultPanel)
        unit.resultsPanel = resultPanel

        and: "a search panel is visible"
        FrameFixture f = GuiActionRunner.execute([executeInEDT: {
            final d = unit.searchPanel()
            final f = new JFrame()
            f.add(d)
            new FrameFixture(f)
        }] as GuiQuery)
        f.show()

        when: "the search field as text 'random"
        f.textBox("search-textfield").enterText("random")

        and: "then when escape is pushed"
        f.textBox("search-textfield").pressKey(KeyEvent.VK_ESCAPE)

        then: "then the text field should be empty"
        f.textBox("search-textfield").requireEmpty()

        and: "the client services should have been queried and the results notified"
        6 * definitionsClientService.findDefinition(1) >> 1
        6 * resultPanel.notifyOfDataChange([1])
        1 * resultPanel.notifyOfDataChange([])

        and: "Cleaup"
        f.cleanUp()
    }
}
