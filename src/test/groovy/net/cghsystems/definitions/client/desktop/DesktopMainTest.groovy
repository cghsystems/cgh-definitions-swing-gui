package net.cghsystems.definitions.client.desktop

import spock.lang.Specification
import org.springframework.context.ApplicationContext
import net.cghsystems.definitions.client.DefinitionsClientService

/**
 * @author: chris
 * @date: 06/12/2012
 */
class DesktopMainTest extends Specification {

    def "Should display sorry GUI if there are not services available"() {
        DesktopMain unit = new DesktopMain();

        given: "an application context"
        final ctx = Mock(ApplicationContext)
        unit.ctx = ctx

        and: "a definitions services client"
        final servicesClient = Mock(DefinitionsClientService)

        and: "a services client"
        final definitionsDesktopClient = Mock(DefinitionsDesktopClient)

        when: "the application is started"
        unit.start()

        then: "the context should return the servicesClient"
        1 * ctx.getBean("definitionsClientService") >> servicesClient

        and: "a desktop gui"
        1 * ctx.getBean("definitionsDesktopClient") >> definitionsDesktopClient

        and: "The services client should return that there are no services available "
        1 * servicesClient.isAvailable() >> false

        and: "the definitionsDesktopClient should siaply the sorry message"
        1 * definitionsDesktopClient.showSorry()
    }

    def "Should display main GUI if there are services available"() {
        DesktopMain unit = new DesktopMain();

        given: "an application context"
        final ctx = Mock(ApplicationContext)
        unit.ctx = ctx

        and: "a definitions services client"
        final servicesClient = Mock(DefinitionsClientService)

        and: "a services client"
        final definitionsDesktopClient = Mock(DefinitionsDesktopClient)

        when: "the application is started"
        unit.start()

        then: "the context should return the servicesClient"
        1 * ctx.getBean("definitionsClientService") >> servicesClient

        and: "a desktop gui"
        1 * ctx.getBean("definitionsDesktopClient") >> definitionsDesktopClient

        and: "The services client should return that there are services available "
        1 * servicesClient.isAvailable() >> true

        and: "the definitionsDesktopClient should siaply the sorry message"
        1 * definitionsDesktopClient.showMain()
    }
}
