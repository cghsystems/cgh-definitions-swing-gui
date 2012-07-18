package net.cghsystems.definitions.ui

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import groovy.transform.TupleConstructor

/**
 * The domain class that contain all of the properties required for a definition.
 * 
 * @author chris
 *
 */
@ToString
@EqualsAndHashCode
@TupleConstructor
class Definition {
    String id, name, definition, description
    Long definitionCategoryId
}