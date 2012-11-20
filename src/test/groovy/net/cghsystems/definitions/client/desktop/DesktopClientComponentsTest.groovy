package net.cghsystems.definitions.client.desktop

import groovy.swing.SwingBuilder
import net.cghsystems.definitions.domain.Definition
import org.fest.swing.annotation.RunsInEDT
import org.fest.swing.edt.FailOnThreadViolationRepaintManager
import org.fest.swing.edt.GuiActionRunner
import org.fest.swing.edt.GuiQuery
import org.fest.swing.fixture.DialogFixture
import org.fest.swing.fixture.FrameFixture
import org.fest.swing.fixture.WindowFixture
import org.junit.Before
import org.junit.Test

import javax.swing.JDialog
import javax.swing.JFrame
import spock.lang.Specification

/**
 * @author: chris
 * @date: 14/11/2012
 */
class DesktopClientComponentsTest extends Specification {

    DesktopClientComponents unit

    void setup() {
        unit = new DesktopClientComponents(swingBuilder: new SwingBuilder())
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
        1 * unit.shutdownListener.notifyOnClose()

        f.cleanUp()
    }

    @RunsInEDT
    def "addOrEditNoteDialogWithDefinition panel should render with Deifnition object"() {

        final title = "title"
        final definition = new Definition(name: "name", definition: "definition", description: "description")

        given: "an add or edit dialog panel"
        WindowFixture f = GuiActionRunner.execute([executeInEDT: {
            JDialog d = unit.addOrEditNoteDialog(title, definition, null)
            new DialogFixture(d)
        }] as GuiQuery)

        when: "The panel is visible"
        f.show()

        then: "The following buttons should be visible and have the correct text"
        ["name", "definition", "description"].each {
            f.label("${it}-label").requireVisible()
            f.textBox("${it}-textfield").requireVisible().requireText(definition."$it")
        }

        and: "The addedit button should be visible"
        f.button("addedit-button").requireVisible()

        f.cleanUp()
    }

    @RunsInEDT
    def "addOrEditNoteDialogWithNullDefinition should render with null definition"() {

        final title = "title"
        final definition = null;

        given: "an add or edit dialog panel with a null definition"
        WindowFixture f = GuiActionRunner.execute([executeInEDT: {
            JDialog d = unit.addOrEditNoteDialog(title, definition, null)
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
}
