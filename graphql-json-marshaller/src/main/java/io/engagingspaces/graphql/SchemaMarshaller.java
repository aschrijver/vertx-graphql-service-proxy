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

package io.engagingspaces.graphql;

import graphql.schema.GraphQLSchema;
import io.engagingspaces.graphql.json.JsonSerializable;
import io.engagingspaces.graphql.schema.Marshaller;
import io.engagingspaces.graphql.schema.Unmarshaller;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

/**
 * Marshals {@link GraphQLSchema} instances to {@link JsonObject} and vice versa.
 *
 * @author <a href="https://github.com/aschrijver/">Arnold Schrijver</a>
 */
public interface SchemaMarshaller {

    /**
     * Marshals the provided {@link GraphQLSchema} instance to its json representation.
     *
     * @param schema the graphql schema
     * @return the json data
     */
    static JsonObject toJson(GraphQLSchema schema) {
        Objects.requireNonNull(schema, "GraphQLSchema cannot be null");
        return ((JsonSerializable) decorateSchema(schema)).toJson();
    }

    /**
     * Un-marshals the provided json data to a {@link GraphQLSchema} instance.
     *
     * @param json the json
     * @return the graphql schema object
     * @throws ClassCastException if expected type does not match un-marshaled object type
     */
    static GraphQLSchema fromJson(JsonObject json) {
        Objects.requireNonNull(json, "Json serialization data cannot be null");
        return Unmarshaller.unmarshall(json, Unmarshaller.createContext(json), null);
    }

    /**
     * Creates a decorated version of the provided {@link GraphQLSchema} instance.
     *
     * @param original the original schema object
     * @return the decorated schema object
     */
    static GraphQLSchema decorateSchema(GraphQLSchema original) {
        Objects.requireNonNull(original, "Original schema object cannot be null");
        return Marshaller.createContext().decoratorOf(original);
    }
}
