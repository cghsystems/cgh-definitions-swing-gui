package net.cghsystems.definitions.client.ws

import net.cghsystems.definitions.domain.Definition
import org.springframework.integration.channel.DirectChannel
import org.springframework.integration.core.MessageHandler
import org.springframework.integration.support.MessageBuilder
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

import javax.annotation.Resource
import org.springframework.integration.channel.QueueChannel
import org.springframework.http.HttpStatus

/**
 * Integration test that requires an an instance of a servlet container, running cgh-definitions-services war in order
 * to perform the REST service operations to test the SI configuration. When run with Gradle jetty will be started
 * by the build when run through any other method then the services will have to be manually started.
 *
 * @author chris
 *
 */
@ContextConfiguration("classpath:META-INF/spring/definitions-services-client-si-context.xml")
class RestConsumerIntegrationTest extends Specification {

    @Resource(name = "findDefinitionsRequestChannel")
    DirectChannel findDefinitionsRequestChannel

    @Resource(name = "findDefinitionsReplyChannel")
    QueueChannel findDefinitionsReplyChannel

    @Resource(name = "createDefinitionsRequestChannel")
    DirectChannel createDefinitionsRequestChannel

    @Resource(name = "deleteDefinitionsRequestChannel")
    DirectChannel deleteDefinitionsRequestChannel

    def "should create and then find and then delete definition from remote rest service"() {

        given: "The expected definition"
        def expected = new Definition("RestConsumerIntegrationTest-1", "RestConsumerIntegrationTest", "RestConsumerIntegrationTest", "RestConsumerIntegrationTest", 49)

        when: "Then expected definition is sent to the createDefinitionsReqestChannel"
        createDefinitionsRequestChannel.send(MessageBuilder.withPayload(expected).build())

        then: "a request to the definition reply channel with the expected definition id should return the expected defintion"
        findDefinitionsRequestChannel.send(MessageBuilder.withPayload(expected.id).build())
        def actual = findDefinitionsReplyChannel.receive(1000).payload
        assert expected == actual: "Definition object: ${actual} returned from the find service does not match the expected value: ${expected}"

        and: "When a request is sent to the delete channel with the expected definition id should delete the expected defintion"
        final id = expected.id
        deleteDefinitionsRequestChannel.send(MessageBuilder.withPayload(id).build())

        and: "a further request is sent to the definition channel with the expected definition id should return nothing"
        findDefinitionsRequestChannel.send(MessageBuilder.withPayload(id).build())
        final actualAfterDeletion = findDefinitionsReplyChannel.receive(1000).payload
        assert HttpStatus.OK == actualAfterDeletion.statusCode: "Definition object: ${actualAfterDeletion.statusCode} returned from the find service does not match zero length string"
        assert null == actualAfterDeletion.body: "Body of a find request after deletiion should be null but was: ${actualAfterDeletion.body}"
    }
}
