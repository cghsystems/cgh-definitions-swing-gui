package net.cghsystems.definitions.client.ws

import static org.junit.Assert.*

import javax.annotation.Resource

import net.cghsystems.notes.ui.Definition

import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.mortbay.jetty.Server
import org.mortbay.jetty.webapp.WebAppContext
import org.springframework.integration.channel.DirectChannel
import org.springframework.integration.core.MessageHandler
import org.springframework.integration.support.MessageBuilder
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner


@RunWith(SpringJUnit4ClassRunner)
@ContextConfiguration("classpath:META-INF/spring/definitions-services-client-si-context.xml")
class RestConsumer {

    @Resource(name = "findDefinitionsRequestChannel")
    DirectChannel requestChannel

    @Resource(name = "findDefinitionsReplyChannel")
    DirectChannel responseChannel

    /** Jetty Server */
    static server

    @BeforeClass
    static void startDefinitionServices() {

        //TODO Find a nice way of handling this (maybe through the use of gradle multi projects
        final warUrl = "/Users/chris/Documents/dev/workspace/cgh-definitions-services/build/libs/cgh-definitions-services-2.0.war"
        final app = new WebAppContext()
        app.setWar(warUrl)
        app.setContextPath("/cgh-definitions-services")
        //Tell the app to run in development mode
        app.setInitParams("spring.profiles.active":"dev")

        server = new Server(8080)
        server.setHandler(app)
        server.start()
    }

    @AfterClass
    static void stopDefinitionServices() {
        server.stop()
    }

    @Test
    void shouldConsume() {
        final expected = new Definition("InsertTestDefinitionData-1", "test-name", "test-def", "test-desc", 49)
        final handle = {
            assert it.getPayload() == expected : "Expected matching Definitions"
        }
        responseChannel.subscribe([handleMessage: handle] as  MessageHandler)
        requestChannel.send(MessageBuilder.withPayload(expected.id).build())
    }
}
