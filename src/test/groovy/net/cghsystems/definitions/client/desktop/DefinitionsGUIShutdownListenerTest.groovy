package net.cghsystems.definitions.client.desktop

import spock.lang.Specification

/**
 * @author: chris
 * @date: 21/11/2012
 */
class DefinitionsGUIShutdownListenerTest extends Specification {

    DefintionsGUIShutdownListener unit

    void setup() {
        unit = new DefintionsGUIShutdownListener()
    }

    def "should notify all registered listeners of shutdown event"() {

        def executed = false

        given: "A GUIShutdownEvent"
        final event = Mock(GUIShutdownEvent)

        and: "an shutdown event closure"
        final shutdownEventClosure = {
            executed = true
        }

        and: "The event is registered with unit"
        unit << event

        when: "the unit is notified of the shutdown event"
        unit.notifyOnClose(shutdownEventClosure)

        then: "The GUIShutdownEvent should be notified"
        1 * event.onClose()

        and: "The shutdown event closure should be executed"
        assert executed: "Expected the shutdownListener to have been executed"
    }

    def "If a shutdown event throws an exception then execution flow should continue"() {
        def executed = false

        given: "a badly behaved event is registered"
        final badlyBehavedEvent = Mock(GUIShutdownEvent)
        unit << badlyBehavedEvent

        and: "A well behaved event is registered"
        final wellBehavedEvent = Mock(GUIShutdownEvent)
        unit << wellBehavedEvent

        and: "an shutdown event closure"
        final shutdownEventClosure = {
            executed = true
        }

        when: "the unit is notified of the shutdown event"
        unit.notifyOnClose(shutdownEventClosure)

        then: "the badly behaved event should be executed"
        1 * badlyBehavedEvent.onClose() >> { throw new RuntimeException("Something mental") }

        and: "The well behaved event should be notified"
        1 * wellBehavedEvent.onClose()

        and: "The shutdown event closure should be executed"
        assert executed: "Expected the shutdownListener to have been executed"
    }

    def "Should handle null shutdown Closure and no listeners"() {
        when: "notifyOnClose is executed wit hno closure argument"
        unit.notifyOnClose()
        then: "execution flow should continue grecefully"
    }
}
