package net.cghsystems.definitions.client.desktop.componentes

import spock.lang.Specification
import groovy.swing.SwingBuilder
import net.cghsystems.definitions.client.desktop.StripeRenderer
import org.fest.swing.fixture.FrameFixture
import org.fest.swing.edt.GuiActionRunner
import javax.swing.JFrame
import org.fest.swing.edt.GuiQuery
import org.fest.swing.fixture.JListFixture
import org.fest.swing.core.BasicRobot
import org.fest.swing.fixture.JScrollPaneFixture
import net.cghsystems.definitions.domain.Definition
import org.fest.swing.annotation.RunsInEDT

/**
 * @author: chris
 * @date: 22/11/2012
 */
class ResultPanelTest extends Specification
{

    ResultPanel unit

    void setup() {
        def builder = new SwingBuilder()
        this.unit = new ResultPanel(swingBuilder: builder, stripeRenderer: new StripeRenderer(builder: builder))
    }

    @RunsInEDT
    def "Should display and update results list"() {

        given: "A ResultPanel"
        FrameFixture f = GuiActionRunner.execute([executeInEDT: {
            final f = new JFrame()
            f.add(unit.resultPanel())
            new FrameFixture(f)
        }] as GuiQuery)

        when: "The result panel is visible"
        f.show()

        then: "The results lists should be emtpy"
        f.list("resultList").requireNoSelection()

        and: "the ResultPanel is notified of new data"
        unit.notifyOfDataChange([new Definition(name: "name", definition: "Definition", description: "Description")])

        then: "The result list should have a single member"
        f.list("resultList").requireItemCount(1)
    }
}
