package net.cghsystems.definitions.rest.client

import net.cghsystems.definitions.ui.Definition
import org.springframework.integration.MessageChannel
import org.springframework.integration.channel.DirectChannel
import org.springframework.integration.channel.RendezvousChannel
import org.springframework.integration.support.MessageBuilder

import javax.annotation.Resource

class DefinitionsRestEndpoint {

    @Resource(name = "createDefinitionsRequestChannel")
    MessageChannel createDefinitionsRequestChannel

    @Resource(name = "deleteDefinitionsRequestChannel")
    MessageChannel deleteDefinitionsRequestChannel

    @Resource(name = "findDefinitionsRequestChannel")
    MessageChannel findDefinitionsRequestChannel

    void deleteDefinition(id) {
        deleteDefinitionsRequestChannel.send(MessageBuilder.withPayload(id).build())
    }

    void createDefinition(definition) {
        def msg = MessageBuilder.withPayload(definition).build()
        createDefinitionsRequestChannel.send(msg)
    }

    def findDefinition(id) {
        RendezvousChannel reply = new RendezvousChannel()
        final message = MessageBuilder.withPayload(id).setReplyChannel(reply).build()

        Thread.start {
            findDefinitionsRequestChannel.send(message)
        }

        reply.receive(2000).payload
    }
}
