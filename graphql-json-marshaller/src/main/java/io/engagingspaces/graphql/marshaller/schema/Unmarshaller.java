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

package io.engagingspaces.graphql.marshaller.schema;

import graphql.schema.DataFetcher;
import graphql.schema.GraphQLSchema;
import graphql.schema.GraphQLType;
import graphql.schema.StaticDataFetcher;
import io.engagingspaces.graphql.marshaller.SchemaMarshallerOptions;
import io.engagingspaces.graphql.marshaller.json.PropNames;
import io.engagingspaces.graphql.marshaller.schema.decorators.GraphQLInterfaceTypeDO;
import io.engagingspaces.graphql.marshaller.schema.impl.SchemaContextImpl;
import io.engagingspaces.graphql.marshaller.schema.decorators.GraphQLObjectTypeDO;
import io.vertx.core.json.JsonObject;

import java.lang.reflect.Constructor;
import java.util.List;

/**
 *
 * @author <a href="https://github.com/aschrijver/">Arnold Schrijver</a>
 */
public interface Unmarshaller {

    String DECORATOR_PACKAGE = "io.engagingspaces.graphql.marshaller.schema.decorators";
    String DECORATOR_POSTFIX = "DO";
    String DOT = ".";

    /**
     * Creates a new schema context.
     *
     * @param rootJson the serialized json data
     * @return the schema context
     */
    static SchemaContext createContext(JsonObject rootJson) {
        return new SchemaContextImpl(SchemaMarshallerOptions.create(), rootJson);
    }

    /**
     * Creates a new schema context for un-marshalling.
     *
     * @param options  the marshaller options
     * @param rootJson the serialized json data
     * @return the schema context
     */
    static SchemaContext createContext(SchemaMarshallerOptions options, JsonObject rootJson) {
        return new SchemaContextImpl(options == null ? SchemaMarshallerOptions.create() : options, rootJson);
    }

    /**
     * Creates a new schema context, if none was provided.
     *
     * @param context  the schema context to use, or null
     * @param rootJson the serialized json data
     * @return the provided context, or a newly created schema context
     */
    static SchemaContext createContextIfMissing(SchemaContext context, JsonObject rootJson) {
        if (context == null) {
            return createContext(rootJson);
        }
        return context;
    }

    /**
     * Used internally for un-marshaling of schema objects.
     *
     * @param json    the json object to un-marshall
     * @param context the schema context
     * @param parent  the parent schema decorator object (can be null)
     * @param <T>     type parameter indicating the schema object type to return
     * @param <U>     type parameter indicating the type of the parent schema object
     * @return the un-marshaled schema decorator object
     */
    @SuppressWarnings("unchecked")
    static <T, U> T unmarshall(JsonObject json, SchemaContext context, U parent) {
        String original = json.getString(PropNames.MARSHALED_TYPE);
        if (StaticDataFetcher.class.getName().equals(original)) {
            original = DataFetcher.class.getName();
        } else if (json.containsKey(PropNames.SCHEMAS)) {
            original = GraphQLSchema.class.getName();
        } else if (original == null) {
            throw new IllegalStateException("Failed to unmarshall, incorrect format or missing marshaling data");
        }

        String marshallToType = DECORATOR_PACKAGE + original.substring(original.lastIndexOf(DOT)) + DECORATOR_POSTFIX;
        try {
            Class<?> clazz = Class.forName(marshallToType);
            Constructor<?> jsonConstructor;
            if (parent == null) {
                jsonConstructor = clazz.getConstructor(JsonObject.class, SchemaContext.class);
                return (T) jsonConstructor.newInstance(json, context);
            }
            Class<?> parentClass = parent.getClass();
            if (parent instanceof GraphQLObjectTypeDO || parent instanceof GraphQLInterfaceTypeDO) {
                parentClass = GraphQLType.class;
            }
            jsonConstructor = clazz.getConstructor(JsonObject.class, SchemaContext.class, parentClass);
            return (T) jsonConstructor.newInstance(json, context, parent);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to marshal '" + original + "' to: " + marshallToType, ex);
        }
    }

    /**
     * Gets the GraphQL schema decorator object was previously un-marshaled from the provided Json serialization data.
     * <p>
     * If not found in the list of decorated types, the json is first un-marshaled, registered and returned.
     *
     * @param json   the serialized json
     * @param parent the parent schema decorator object
     * @param <T>    type parameter indicating the type of the returned schema decorator object
     * @param <U>    type parameter indicating the type of the parent schema object
     * @return the schema decorator object
     */
    <T extends SchemaDecorator, U extends SchemaDecorator> T dereference(Object json, U parent);

    /**
     * Un-marshall the GraphQL schema decorator object from the provided Json serialization data.
     *
     * @param json the serialized json
     * @param <T>  type parameter indicating the type of the returned schema decorator object
     * @return the schema decorator object
     */
    <T extends SchemaDecorator> T unmarshall(JsonObject json);

    /**
     * Un-marshall the GraphQL schema decorator object from the provided Json serialization data.
     *
     * @param json   the serialized json
     * @param parent the parent schema decorator object
     * @param <T>    type parameter indicating the type of the returned schema decorator object
     * @param <U>    type parameter indicating the type of the parent schema object
     * @return the schema decorator object
     */
    <T extends SchemaDecorator, U extends SchemaDecorator> T unmarshall(JsonObject json, U parent);

    /**
     * Un-marshall the list of GraphQL schema decorator objects from the {@link JsonObject} or
     * {@link io.vertx.core.json.JsonArray} specified by the {@code listKey} parameter.
     *
     * @param json    the serialized json that contains the list
     * @param listKey the key where the list is located
     * @param <T>     type parameter indicating the type of the return list values
     * @return the list of schema decorator objects
     */
    <T> List<T> unmarshallList(JsonObject json, String listKey);


    /**
     * Un-marshall the list of GraphQL schema decorator objects from the {@link JsonObject} or
     * {@link io.vertx.core.json.JsonArray} specified by the {@code listKey} parameter, using the provided parent
     * object to un-marshall individual list values.
     *
     * @param json    the serialized json that contains the list
     * @param listKey the key where the list is located
     * @param parent  the parent schema decorator object
     * @param <T>     type parameter indicating the type of the return list values
     * @param <U>     type parameter indicating the type of the parent schema object
     * @return the list of schema decorator objects
     */
    <T, U extends SchemaDecorator> List<T> unmarshallList(JsonObject json, String listKey, U parent);
}
