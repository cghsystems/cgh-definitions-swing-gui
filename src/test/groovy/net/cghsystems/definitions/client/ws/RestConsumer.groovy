package net.cghsystems.definitions.client.ws

import static org.junit.Assert.*

import javax.annotation.Resource

import net.cghsystems.definitions.ui.Definition

import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.mortbay.jetty.Server
import org.mortbay.jetty.webapp.WebAppContext
import org.springframework.http.HttpStatus
import org.springframework.integration.channel.DirectChannel
import org.springframework.integration.core.MessageHandler
import org.springframework.integration.support.MessageBuilder
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner


/**
 * Integration test that will Spin up an instance of Jetty, run cgh-definitions-servives 
 * war and perform the REST service operations to test the SI configuration
 * 
 * @author chris
 *
 */
@RunWith(SpringJUnit4ClassRunner)
@ContextConfiguration("classpath:META-INF/spring/definitions-services-client-si-context.xml")
class RestConsumer {

    @Resource(name = "findDefinitionsRequestChannel")
    DirectChannel findDefinitionsRequestChannel

    @Resource(name = "findDefinitionsReplyChannel")
    DirectChannel findDefinitionsReplyChannel

    @Resource(name = "createDefinitionsRequestChannel")
    DirectChannel createDefinitionsRequestChannel

    @Resource(name = "deleteDefinitionsRequestChannel")
    DirectChannel deleteDefinitionsRequestChannel

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

    /**
     * Given a {@link Definition}
     * When Create Then Should Create
     * And When Find Then Should Find
     * And When Delete Then Should Delete
     */
    @Test
    void shouldCreateAndThenFindAndThenDeleteDefinitionFromRemoteRestService() {
        def expected = new Definition("RestConsumer-1", "RestConsumer", "RestConsumer", "RestConsumer", 49)

        //Create
        createDefinitionsRequestChannel.send(MessageBuilder.withPayload(expected).build())


        //Find
        //Ensures that any returned payload matches the expected.
        final handle = {
            def actual = it.getPayload()
            assert  actual == expected : "Expected matching Definitions"
        }
        findDefinitionsReplyChannel.subscribe([handleMessage: handle] as  MessageHandler)
        //Call the remote Rest service
        findDefinitionsRequestChannel.send(MessageBuilder.withPayload(expected.id).build())


        //Delete
        final id = expected.id
        deleteDefinitionsRequestChannel.send(MessageBuilder.withPayload(id).build())
        expected = HttpStatus.OK //Change type of expected as the original definition should have been deleted
        findDefinitionsRequestChannel.send(MessageBuilder.withPayload(id).build())
    }
}
