package net.cghsystems.definitions.client.desktop

import net.cghsystems.definitions.client.desktop.ioc.DesktopApplicationContext
import org.springframework.context.annotation.AnnotationConfigApplicationContext

/**
 * Main class of the Definitions Desktop client.
 *
 * @author: chris
 * @date: 14/11/2012
 */
class DesktopMain {

    static main(args) {
        System.setProperty("spring.profiles.default", "cloud")
        new DesktopMain().start()
    }

    private AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(DesktopApplicationContext)

    /**
     * Starts the swing client. If no server connection can be established then a warning dialog is shown to hte user.
     */
    final void start() {

        ctx.registerShutdownHook()

        final clientService = ctx.getBean("definitionsClientService")
        final gui = ctx.getBean("definitionsDesktopClient")


        if (clientService.isAvailable()) {
            gui.showMain()
        } else {
            gui.showSorry();
        }
    }
}
