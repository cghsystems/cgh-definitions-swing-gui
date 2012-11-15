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

/**
 * @author: chris
 * @date: 14/11/2012
 */
class DesktopClientComponentsTest {

    DesktopClientComponents unit

    @Before
    void before() {
        unit = new DesktopClientComponents(swingBuilder: new SwingBuilder())
        FailOnThreadViolationRepaintManager.install()
    }

    @Test
    @RunsInEDT
    void buttonPanel() {

        FrameFixture f = GuiActionRunner.execute([executeInEDT: {
            final f = new JFrame()
            f.add(unit.buttonPanel())
            new FrameFixture(f)
        }] as GuiQuery)

        f.show()

        ["add-definition", "edit-definition", "delete-definition", "close"].each {
            f.button(it).requireVisible()
        }

        f.cleanUp()
    }

    @Test
    @RunsInEDT
    void addOrEditNoteDialogWithDefinition() {

        final title = "title"
        final definition = new Definition(name: "name", definition: "definition", description: "description")

        WindowFixture f = GuiActionRunner.execute([executeInEDT: {
            JDialog d = unit.addOrEditNoteDialog(title, definition, null)
            new DialogFixture(d)
        }] as GuiQuery)

        f.show()

        ["name", "definition", "description"].each {
            f.label("${it}-label").requireVisible()
            f.textBox("${it}-textfield").requireVisible().requireText(definition."$it")
        }

        f.button("addedit-button").requireVisible()

        f.cleanUp()
    }

    @Test
    @RunsInEDT
    void addOrEditNoteDialogWithNullDefinition() {

        final title = "title"
        final definition = null;

        WindowFixture f = GuiActionRunner.execute([executeInEDT: {
            JDialog d = unit.addOrEditNoteDialog(title, definition, null)
            new DialogFixture(d)
        }] as GuiQuery)

        f.show()

        ["name", "definition", "description"].each {
            f.label("${it}-label").requireVisible()
            f.textBox("${it}-textfield").requireVisible().requireText("")
        }

        f.button("addedit-button").requireVisible()

        f.cleanUp()
    }
}
