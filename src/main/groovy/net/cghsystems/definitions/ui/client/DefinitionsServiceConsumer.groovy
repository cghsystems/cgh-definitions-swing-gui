package net.cghsystems.definitions.ui.client

import org.springframework.integration.MessageChannel
import org.springframework.integration.support.MessageBuilder
import net.cghsystems.definitions.domain.Definition
import org.springframework.integration.channel.QueueChannel
import org.springframework.integration.core.PollableChannel

/**
 * @author chris
 */
class DefinitionsServiceConsumer {

    MessageChannel deleteChannel

    MessageChannel createChannel

    MessageChannel findRequestChannel

    PollableChannel findReplyChannel


    void deleteDefinition(Integer id) {
       deleteChannel.send(MessageBuilder.withPayload(id).build())
    }

    void createDefinition(Definition definition) {
        createChannel.send(MessageBuilder.withPayload(definition).build())
    }

    Definition findDefinition(Integer id) {
        findRequestChannel.send(MessageBuilder.withPayload(id).build())
        findReplyChannel.receive(1000).payload
    }
}
