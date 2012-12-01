package net.cghsystems.definitions.client

import net.cghsystems.definitions.domain.Definition
import org.springframework.integration.MessageChannel
import org.springframework.integration.core.PollableChannel
import org.springframework.integration.support.MessageBuilder

import javax.annotation.Resource
import groovy.util.logging.Log4j

/**
 * The outbound service for clients of any type to provide mechanism of communication with the definition services.
 *
 * @author chris
 */
@Log4j
class DefinitionsClientService {

    @Resource(name = "deleteDefinitionsRequestChannel")
    private MessageChannel deleteChannel

    @Resource(name = "createDefinitionsRequestChannel")
    private MessageChannel createChannel

    @Resource(name = "findDefinitionsRequestChannel")
    private MessageChannel findRequestChannel

    @Resource(name = "findDefinitionsReplyChannel")
    private PollableChannel findReplyChannel

    @Resource(name = "pingChannel")
    private MessageChannel pingChannel

    @Resource(name = "createDefinitionsReplyChannel")
    private PollableChannel createReplyChannel

    void deleteDefinition(id) {
        deleteChannel.send(MessageBuilder.withPayload(id).build())
    }

    Definition createDefinition(Definition definition) {
        try {
            createChannel.send(MessageBuilder.withPayload(definition).build())
            createReplyChannel.receive(1000).payload
        } catch (e) {
            e.printStackTrace()
            throw new IllegalStateException("Unable to communicate with the Definitions server at this time")
        }
    }

    /**
     *
     * @param id
     * @return either the HTTP Status returned from the payload if there is no definition found or the found definition.
     * TODO Look at multiple returns in groovy
     */
    def findDefinition(id) {
        findRequestChannel.send(MessageBuilder.withPayload(id).build())
        findReplyChannel.receive(1000).payload
    }

    /**
     * @return a boolean value to determine if the communication with the backend services can be established. true for
     * service is available false for it is not.
     */
    boolean isAvailable() {
        try {
            pingChannel.send(MessageBuilder.withPayload("A ping request from DefinitionsClientService").build())
            true
        } catch (e) {
            log.error("Exception talking to the services", e)
            false
        }
    }

    /**
     * Hits the services to get available categories
     *
     * @return a list containing all of the available categories to display
     */
    def findAllCategories() {
        return ["test1", "test2"]
    }
}
