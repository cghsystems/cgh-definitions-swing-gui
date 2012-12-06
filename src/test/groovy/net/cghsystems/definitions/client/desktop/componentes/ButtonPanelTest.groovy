package net.cghsystems.definitions.client.desktop.componentes

import groovy.swing.SwingBuilder
import net.cghsystems.definitions.client.DefinitionsClientService
import net.cghsystems.definitions.client.desktop.DefintionsGUIShutdownListener
import net.cghsystems.definitions.client.desktop.StripeRenderer
import net.cghsystems.definitions.domain.Definition
import org.fest.swing.annotation.RunsInEDT
import org.fest.swing.edt.FailOnThreadViolationRepaintManager
import org.fest.swing.edt.GuiActionRunner
import org.fest.swing.edt.GuiQuery
import org.fest.swing.fixture.DialogFixture
import org.fest.swing.fixture.FrameFixture
import org.fest.swing.fixture.WindowFixture
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification

import javax.swing.JDialog
import javax.swing.JFrame

/**
 * @author: chris
 * @date: 14/11/2012
 */
class ButtonPanelTest extends Specification {


    ButtonPanel unit

    void setup() {
        final builder = new SwingBuilder()
        unit = new ButtonPanel(swingBuilder: builder)
        FailOnThreadViolationRepaintManager.install()
    }

    @RunsInEDT
    def "button panel should render"() {

        given: "A button panel"
        FrameFixture f = GuiActionRunner.execute([executeInEDT: {
            final f = new JFrame()
            f.add(unit.buttonPanel())
            new FrameFixture(f)
        }] as GuiQuery)

        when: "button panel is visiible"
        f.show()

        then: "The following buttons should display"
        ["add-definition", "edit-definition", "delete-definition", "close"].each {
            f.button(it).requireVisible()
        }

        f.cleanUp()
    }

    @RunsInEDT
    def "button panel close button should call shutdown listener"() {

        given: "A button panel"
        FrameFixture f = GuiActionRunner.execute([executeInEDT: {
            final f = new JFrame()
            f.add(unit.buttonPanel())
            new FrameFixture(f)
        }] as GuiQuery)


        and: "The panel has a registered shutdownlistener"
        unit.shutdownListener = Mock(DefintionsGUIShutdownListener)

        when: "Button panel is visible"
        f.show()

        and: "The close button is clicked"
        f.button("close").click()

        then: "The shutdown listener should receive request"
        1 * unit.shutdownListener.notifyOnClose(_)

        f.cleanUp()
    }

    @RunsInEDT
    def "addOrEditNoteDialog should render with null definition"() {

        final title = "title"
        final definition = null;

        given: "an add or edit dialog panel with a null definition"
        WindowFixture f = GuiActionRunner.execute([executeInEDT: {
            JDialog d = unit.addOrEditDefinitionDialog(title, definition, null)
            new DialogFixture(d)
        }] as GuiQuery)

        when: "The panel is visible"
        f.show()

        then: "The following buttons should be visible and have the correct text"
        ["name", "definition", "description"].each {
            f.label("${it}-label").requireVisible()
            f.textBox("${it}-textfield").requireVisible().requireText("")
        }

        and: "The addedit button should be visible"
        f.button("addedit-button").requireVisible()

        f.cleanUp()
    }

    @RunsInEDT
    def "addOrEditNoteDialogWithNullDefinition should send add request when addOrEditNoteDialog is passed null definition"() {

        final title = "title"
        boolean executed = false

        def (expectedName, expectedDefinition, expectedDescription) = ["name", "definition", "description"]
        final addClosure = { id, name, definition, description ->
            assert name == expectedName
            assert definition == expectedDefinition
            assert description == expectedDescription
            executed = true
        }

        given: "an add or edit dialog panel with a null definition"
        WindowFixture f = GuiActionRunner.execute([executeInEDT: {
            JDialog d = unit.addOrEditDefinitionDialog(title, null, addClosure)
            new DialogFixture(d)
        }] as GuiQuery)

        when: "The panel is visible"
        f.show()

        then: "a name, definition and description are added"
        f.textBox("name-textfield").setText(expectedName)
        f.textBox("definition-textfield").setText(expectedDefinition)
        f.textBox("description-textfield").setText(expectedDescription)

        and: "when the add button is clicked"
        f.button("addedit-button").click()

        and: "The addClosure should have been called"
        assert executed: "The addClossure should have been executed"

        f.cleanUp()
    }

    @RunsInEDT
    def "addOrEditNoteDialogWithNullDefinition should send edit request addOrEditNoteDialog is passed existing definition"() {

        def title = "title"
        def executed = false
        def (expectedId, expectedName, expectedDefinition, expectedDescription) = ["1", "edited-name", "edited-definition", "edited-description"]
        final definitionObject = new Definition(id: 1, name: "expectedName", definition: "expectedDefinition", description: "expectedDescription")

        final addClosure = { id, name, definition, description ->
            assert id == expectedId
            assert name == expectedName
            assert definition == expectedDefinition
            assert description == expectedDescription
            executed = true
        }

        given: "an add or edit dialog panel with a definition"
        WindowFixture f = GuiActionRunner.execute([executeInEDT: {
            JDialog d = unit.addOrEditDefinitionDialog(title, definitionObject, addClosure)
            new DialogFixture(d)
        }] as GuiQuery)

        when: "The panel is visible"
        f.show()

        then: "The following buttons should be visible and have the correct text"
        ["name", "definition", "description"].each {
            f.label("${it}-label").requireVisible()
            f.textBox("${it}-textfield").requireVisible().requireText(definitionObject."$it")
        }

        and: "when the text fields are changed"
        f.textBox("name-textfield").setText(expectedName)
        f.textBox("definition-textfield").setText(expectedDefinition)
        f.textBox("description-textfield").setText(expectedDescription)

        and: "when the add button is clicked again"
        f.button("addedit-button").click()

        and: "The addClosure should have been called"
        assert executed: "The edit closure should have been executed"

        f.cleanUp()
    }

    def "should execute addDefinitionAction"() {

        def description = "description"
        def definition = "definition"
        def name = "name"
        def generatedId = "generateId"

        final definitionObject = new Definition(name: name, description: description,
                definition: definition,
                definitionCategoryId: 1)


        final definitionObjectWithId = new Definition(id: generatedId, name: name, description: description,
                definition: definition,
                definitionCategoryId: 1)


        given: "a definitionsClientService"
        final definitionsClientService = Mock(DefinitionsClientService)
        unit.definitionsClientService = definitionsClientService

        and: "and a result panel"
        final resultsPanel = Mock(ResultPanel)
        unit.resultsPanel = resultsPanel

        when: "addDefinitionIsExecuted"
        unit.addOrUpdateDefinitionAction.call(generatedId, name, definition, description)

        then: "The definition client service should be called with the expected definition"
        1 * definitionsClientService.createDefinition({it == definitionObject}) >> definitionObjectWithId

        and: "a new search is made to get the new definitions"
        1 * definitionsClientService.findDefinition(generatedId) >> definitionObjectWithId

        and: "the results panel is called to update the view with the new data"
        1 * resultsPanel.notifyOfDataChange({it == [definitionObjectWithId]})
    }

    @RunsInEDT
    def  "should delete definition"() {

    }

    @RunsInEDT
    @Ignore("Need to investigate how to get FEST to talk to the dialog")
    def "should handle nothing to delete"() {

        given: "a frame"
        @Shared JFrame frame

        FrameFixture f = GuiActionRunner.execute([executeInEDT: {
            frame = new JFrame()
            new FrameFixture(frame)
        }] as GuiQuery)

        and: "A Stripe Renderer"
        def stripeRenderer = Mock(StripeRenderer)
        unit.stripeRenderer = stripeRenderer

        when: "delete is called"
        f.show()
        unit.deleteNoteDialogAction.call(frame)

        then: "OptionDialo should be displayed"
        f.dialog("dialog0").label("OptionPane.label").requireText("Please select a Note to delete")

        and: "something"
        1 * stripeRenderer.getCurrentlySelected() >> null

        f.cleanUp()
    }
}
