package net.cghsystems.definitions.client.desktop

import net.cghsystems.definitions.client.desktop.ioc.DesktopApplicationContext
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.AnnotationConfigApplicationContext

/**
 * Main class of the Definitions Desktop client.
 *
 * @author: chris
 * @date: 14/11/2012
 */
class DesktopMain {

    static main(args) {
        new DesktopMain().start()
    }

    ApplicationContext ctx = new AnnotationConfigApplicationContext(DesktopApplicationContext)

    final start() {

        final clientService = ctx.getBean("definitionsClientService")
        if (clientService.isAvailable()) {
            final gui = ctx.getBean("definitionsDesktopClient")
            gui.show()
        } else {
            gui.showSorry();
        }
    }
}
