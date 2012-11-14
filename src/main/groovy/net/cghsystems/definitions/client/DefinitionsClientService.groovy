package net.cghsystems.definitions.client

import org.springframework.integration.MessageChannel
import org.springframework.integration.support.MessageBuilder
import net.cghsystems.definitions.domain.Definition

import org.springframework.integration.core.PollableChannel
import javax.annotation.Resource
import org.springframework.integration.MessageHandlingException

/**
 * The outbound service for clients of any type to provide mechanism of communication with the definition services.
 *
 * @author chris
 */
class DefinitionsClientService {

    @Resource(name = "deleteDefinitionsRequestChannel")
    MessageChannel deleteChannel

    @Resource(name = "createDefinitionsRequestChannel")
    MessageChannel createChannel

    @Resource(name = "findDefinitionsRequestChannel")
    MessageChannel findRequestChannel

    @Resource(name = "findDefinitionsReplyChannel")
    PollableChannel findReplyChannel


    void deleteDefinition(Integer id) {
       deleteChannel.send(MessageBuilder.withPayload(id).build())
    }

    void createDefinition(Definition definition) {
        try {
            createChannel.send(MessageBuilder.withPayload(definition).build())
        } catch (e) {
             throw new IllegalStateException("Unable to communicate with the Definitions server at this time")
        }
    }

    Definition findDefinition(Integer id) {
        findRequestChannel.send(MessageBuilder.withPayload(id).build())
        findReplyChannel.receive(1000).payload
    }

    List findAllDefinitionsForCategory(Integer categoryId) {
        throw null
    }
}
