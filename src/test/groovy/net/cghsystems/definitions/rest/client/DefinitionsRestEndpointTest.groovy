package net.cghsystems.definitions.rest.client

import net.cghsystems.definitions.ui.Definition
import net.cghsystems.definitions.ui.ioc.DefinitionsConfig
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.http.HttpStatus
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

import javax.annotation.Resource

/**
 * Integration test that will Spin up an instance of Jetty, run cgh-definitions-servives 
 * war and perform the REST service operations to test the SI configuration
 *
 * @author chris
 *
 */
@RunWith(SpringJUnit4ClassRunner)
@ContextConfiguration(classes = DefinitionsConfig)
class DefinitionsRestEndpointTest {

    @Resource(name = "definitionsRestEndpoint")
    DefinitionsRestEndpoint unit

    /**
     * Given a {@link Definition}
     * When Create Then Should Create
     * And When Find Then Should Find
     * And When Delete Then Should Delete
     */
    @Test
    void shouldCreateAndThenFindAndThenDeleteDefinitionFromRemoteRestService() {
        def expected = new Definition("DefinitionsRestEndpointTest-1", "DefinitionsRestEndpointTest", "DefinitionsRestEndpointTest", "DefinitionsRestEndpointTest", 49)

        //Create
        unit.createDefinition(expected)

        //Find
        def actual = unit.findDefinition(expected.id)
        assert actual == expected: "Expected matching Definitions"

        //Delete
        final id = expected.id
        unit.deleteDefinition(id)

        actual = unit.findDefinition(id)
        assert actual.statusCode == HttpStatus.OK: "Expected matching Definitions"
    }
}
