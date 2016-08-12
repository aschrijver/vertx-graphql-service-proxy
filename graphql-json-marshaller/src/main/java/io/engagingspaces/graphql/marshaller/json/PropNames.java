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

package io.engagingspaces.graphql.marshaller.json;

/**
 * Constants for GraphQL properties that are used as json keys.
 *
 * @author <a href="https://github.com/aschrijver/">Arnold Schrijver</a>
 */
public final class PropNames {
    public static final String ARGUMENTS = "arguments";
    public static final String DATA_FETCHER = "dataFetcher";
    public static final String DEFAULT_VALUE = "defaultValue";
    public static final String DEPRECATION_REASON = "deprecationReason";
    public static final String DESCRIPTION = "description";
    public static final String DICTIONARY = "dictionary";
    public static final String DIRECTIVES = "directives";
    public static final String FIELD_DEFINITIONS = "fieldDefinitions";
    public static final String FIELDS = "fields";
    public static final String ID = "id";
    public static final String INTERFACES = "interfaces";
    public static final String IS_ON_FIELD = "isOnField";
    public static final String IS_ON_FRAGMENT = "isOnFragment";
    public static final String IS_ON_OPERATION = "isOnOperation";
    public static final String MUTATION_TYPE = "mutationType";
    public static final String NAME = "name";
    public static final String QUERY_TYPE = "queryType";
    public static final String STATIC_VALUE = "staticValue";
    public static final String TYPE = "type";
    public static final String TYPES = "types";
    public static final String TYPE_RESOLVER = "typeResolver";
    public static final String VALUE = "value";
    public static final String VALUES = "values";
    public static final String WRAPPED_TYPE = "wrappedType";

    // Used in json marshaling
    public static final String DATA_FETCHERS = "__dataFetchers";
    public static final String MARSHALED_TYPE = "__marshaled";
    public static final String MARSHALED_TYPE_CLASS = "__marshaledClass";
    public static final String PARENT = "__parent";
    public static final String SCALAR_TYPES = "__scalarTypes";
    public static final String SCHEMA_INTERFACES = "__interfaces";
    public static final String SCHEMA_TYPES = "__types";
    public static final String SCHEMAS = "__schemas";
    public static final String TYPE_RESOLVERS = "__typeResolvers";
}
