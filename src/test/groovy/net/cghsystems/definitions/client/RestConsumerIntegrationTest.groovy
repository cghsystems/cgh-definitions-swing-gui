package net.cghsystems.definitions.client

import net.cghsystems.definitions.domain.Definition
import org.springframework.integration.channel.DirectChannel
import org.springframework.integration.core.MessageHandler
import org.springframework.integration.support.MessageBuilder
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

import javax.annotation.Resource
import org.springframework.integration.channel.QueueChannel
import org.springframework.http.HttpStatus
import net.cghsystems.definitions.client.desktop.ioc.DesktopApplicationContext
import spock.lang.Shared
import org.springframework.test.context.ActiveProfiles

/**
 * TODO Move to integration test folder
 *
 * Integration test that requires an an instance of a servlet container, running cgh-definitions-services war in order
 * to perform the REST service operations to test the SI configuration. When run with Gradle jetty will be started
 * by the build when run through any other method then the services will have to be manually started.
 *
 * @author chris
 *
 */
@ContextConfiguration(classes = [DesktopApplicationContext])
@ActiveProfiles("localhost")
class RestConsumerIntegrationTest extends Specification {

    @Resource(name = "definitionsClientService")
    DefinitionsClientService unit

    def "should establish connection to remote services"() {
        when: "a ping an isAvailable request is sent to the server"
        @Shared result = unit.isAvailable()
        then: "the result should be positive"
        assert result : "Was expecting server communication to be established"
    }

    def "should create and then find and then delete definition from remote rest service"() {

        given: "The expected definition"
        def expected = new Definition("RestConsumerIntegrationTest", "RestConsumerIntegrationTest", "RestConsumerIntegrationTest", 49)

        when: "Then expected definition is sent to the createDefinitionsReqestChannel"
        expected = unit.createDefinition(expected)

        then: "a request to the definition reply channel with the expected definition id should return the expected defintion"
        def actual = unit.findDefinition(expected.id)
        assert expected == actual: "Definition object: ${actual} returned from the find service does not match the expected value: ${expected}"

        and: "When a request is sent to the delete channel with the expected definition id should delete the expected defintion"
        final id = expected.id
        unit.deleteDefinition(id)

        and: "a further request is sent to the definition channel with the expected definition id should return nothing"
        final actualAfterDeletion = unit.findDefinition(id)
        assert HttpStatus.OK == actualAfterDeletion.statusCode: "Definition object: ${actualAfterDeletion.statusCode} returned from the find service does not match zero length string"
        assert null == actualAfterDeletion.body: "Body of a find request after deletiion should be null but was: ${actualAfterDeletion.body}"
    }
}
