package net.cghsystems.definitions.client;


import net.cghsystems.definitions.domain.Definition
import org.springframework.integration.MessageChannel
import org.springframework.test.util.ReflectionTestUtils
import spock.lang.Specification
import org.springframework.integration.core.PollableChannel
import org.springframework.integration.support.MessageBuilder

/**
 * @author chris
 */
public class DefinitionsClientServiceTest extends Specification {

    DefinitionsClientService unit

    void setup() {
        unit = new DefinitionsClientService()
    }

    def "should request a message is deleted"() {

        given: "the unit has a delete definition channel"
        final deleteChannel = Mock(MessageChannel)

        ReflectionTestUtils.setField(unit, "deleteChannel", deleteChannel)

        when: unit.deleteDefinition(1)

        then: "The definition id is sent in the payload of a message submitted to the deleteChannel"
        1 * deleteChannel.send({it.payload == 1})
    }

    def "should find a Definition"() {

        given: "the unit has a find request channel"
        final findRequestChannel = Mock(MessageChannel)
        ReflectionTestUtils.setField(unit, "findRequestChannel", findRequestChannel)

        and: "a find reply channel"
        final findReplyChannel = Mock(PollableChannel)
        ReflectionTestUtils.setField(unit, "findReplyChannel", findReplyChannel)

        and: "the definition object is"
        final expectedDefinition = new Definition(name: "something interesting")
        final expectedDefinitionMessage = MessageBuilder.withPayload(expectedDefinition).build()

        when:
        final actual = unit.findDefinition(1)

        then: "the request channel should have received the expected definition id"
        1 * findRequestChannel.send({it.payload == 1})

        and: "the reply channel should return the expected definition"
        1 * findReplyChannel.receive(1000) >> expectedDefinitionMessage

        and: "the actual returned definition should match the expected"
        assert actual == expectedDefinition
    }


    //Create
    def "should request a message is created"() {

        given: "the unit has a create definition channel"
        final createChannel = Mock(MessageChannel)
        ReflectionTestUtils.setField(unit, "createChannel", createChannel)

        and: "the definitions object is"
        final definition = new Definition(name: "something interesting")

        when: unit.createDefinition(definition)

        then: "the create channel should have received the expected definition"
        1 * createChannel.send({it.payload == definition})
    }

    def "should throw illegal arg if the services cannot be found"() {
        given: "the unit has a crearte definition channel"
        and: "the definintion object is"
        when: "a deinfition is created" //unit.createDefinition(definition)
        then: "An illegal arg should be thrown"
    }
}
