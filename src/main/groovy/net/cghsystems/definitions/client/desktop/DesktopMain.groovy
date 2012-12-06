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
        System.setProperty("spring.profiles.default", "localhost")
        new DesktopMain().init().start()
    }

    /** The application Context instance */
    def ctx;

    /**
     * Start the application context
     */
    DesktopMain init() {
        ctx = new AnnotationConfigApplicationContext(DesktopApplicationContext)
        ctx.registerShutdownHook()
        this
    }

    /**
     * Starts the swing client. If no server connection can be established then a warning dialog is shown to the user.
     */
    final void start() {

        final clientService = ctx.getBean("definitionsClientService")
        final gui = ctx.getBean("definitionsDesktopClient")

        if (clientService.isAvailable()) {
            gui.showMain()
        } else {
            gui.showSorry();
        }
    }
}
