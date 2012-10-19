package net.cghsystems.definitions.ui

import net.cghsystems.definitions.ui.ioc.DefinitionsConfig

import org.springframework.context.annotation.AnnotationConfigApplicationContext

class Main {

    static main(args) {
        final ctx = new AnnotationConfigApplicationContext(DefinitionsConfig)
        final n = ctx.getBean(DefinitionsGUI)
        n.show()
    }
}
