/*
 * Copyright (c) 2016 The original author or authors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 *
 *      The Eclipse Public License is available at
 *      http://www.eclipse.org/legal/epl-v10.html
 *
 *      The Apache License v2.0 is available at
 *      http://www.opensource.org/licenses/apache2.0.php
 *
 * You may elect to redistribute this code under either of these licenses.
 */

package io.engagingspaces.graphql.json;

/**
 * Constants for GraphQL properties that are used as json keys.
 *
 * @author <a href="https://github.com/aschrijver/">Arnold Schrijver</a>
 */
public interface PropNames {
    String ARGUMENTS = "arguments";
    String DATA_FETCHER = "dataFetcher";
    String DEFAULT_VALUE = "defaultValue";
    String DEPRECATION_REASON = "deprecationReason";
    String DESCRIPTION = "description";
    String DICTIONARY = "dictionary";
    String DIRECTIVES = "directives";
    String FIELD_DEFINITIONS = "fieldDefinitions";
    String FIELDS = "fields";
    String ID = "id";
    String INTERFACES = "interfaces";
    String IS_ON_FIELD = "isOnField";
    String IS_ON_FRAGMENT = "isOnFragment";
    String IS_ON_OPERATION = "isOnOperation";
    String MUTATION_TYPE = "mutationType";
    String NAME = "name";
    String QUERY_TYPE = "queryType";
    String STATIC_VALUE = "staticValue";
    String TYPE = "type";
    String TYPES = "types";
    String TYPE_RESOLVER = "typeResolver";
    String VALUE = "value";
    String VALUES = "values";
    String WRAPPED_TYPE = "wrappedType";

    // Used in json marshaling
    String DATA_FETCHERS = "__dataFetchers";
    String MARSHALED_TYPE = "__marshaled";
    String MARSHALED_TYPE_CLASS = "__marshaledClass";
    String PARENT = "__parent";
    String SCALAR_TYPES = "__scalarTypes";
    String SCHEMA_INTERFACES = "__interfaces";
    String SCHEMA_TYPES = "__types";
    String SCHEMAS = "__schemas";
    String TYPE_RESOLVERS = "__typeResolvers";
}
