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

package io.engagingspaces.graphql.schema;

import graphql.schema.DataFetcher;
import graphql.schema.GraphQLScalarType;
import graphql.schema.TypeResolver;
import io.engagingspaces.graphql.SchemaMarshallerOptions;
import io.engagingspaces.graphql.json.JsonReference;
import io.engagingspaces.graphql.schema.decorators.DataFetcherDO;
import io.engagingspaces.graphql.schema.decorators.GraphQLScalarTypeDO;
import io.engagingspaces.graphql.schema.decorators.GraphQLSchemaDO;
import io.engagingspaces.graphql.schema.decorators.TypeResolverDO;

import java.util.Map;

/**
 * Schema context that is passed when decorating a {@link graphql.schema.GraphQLSchema}
 * as a {@link GraphQLSchemaDO}, marshalling it to JSON and un-marshaling it again
 * to a {@link io.engagingspaces.graphql.schema.decorators.GraphQLSchemaDO} instance.
 *
 * @author <a href="https://github.com/aschrijver/">Arnold Schrijver</a>
 */
public interface SchemaContext extends Marshaller, Unmarshaller {

    /**
     * Used to pass argument checks in dummy super constructors (and delegate to decorator instead).
     */
    String EMPTY = "";

    /**
     * Gets the marshaller options that are used for marshaling and un-marshaling schema types.
     *
     * @return the marshaller options
     */
    SchemaMarshallerOptions options();

    /**
     * Register a new decorator object with the schema context.
     * <p>
     * When marshalling the {@code original} parameter must contain the original schema object that is decorated.
     * When un-marshalling the {@code original} parameter holds the Json reference string of the decorated schema type.
     *
     * @param decorator the decorated schema object
     * @return the json reference instance to the decorated schema object type
     */
    JsonReference registerDecorator(SchemaDecorator decorator);

    /**
     * Register a new type resolver with the schema context.
     * <p>
     * The type resolver is registered by its ID, and is also registered in the {@code typeDecorators} collection.
     *
     * @param resolver the type resolver to register
     * @return the json reference instance to the decorated type resolver
     */
    JsonReference registerTypeResolver(TypeResolverDO resolver);

    /**
     * Register a new data fetcher with the schema context.
     * <p>
     * The data fetcher is registered by its ID, and is also registered in the {@code dataFetchers} collection.
     *
     * @param dataFetcher the data fetcher to register
     * @return the json reference instance to the decorated data fetcher
     */
    JsonReference registerDataFetcher(DataFetcherDO dataFetcher);

    /**
     * Register a new scalar type with the schema context
     *
     * @param scalarType the scalar type to register
     * @return the json reference instance to the decorated scalar type
     */
    JsonReference registerScalarType(GraphQLScalarTypeDO scalarType);

    /**
     * Gets the map of registered decorated schema object types. The keys of the map are the {@link JsonReference}s
     * location of the decorator in the GraphQL schema.
     *
     * @return the decorated types
     */
    Map<Object, SchemaDecorator> getDecoratedTypes();

    /**
     * Gets the list of registered decorated type resolvers.
     *
     * @return the type resolvers
     */
    Map<String, TypeResolver> getTypeResolvers();

    /**
     * Gets the list of registered decorated data fetchers.
     *
     * @return the data fetchers
     */
    Map<String, DataFetcher> getDataFetchers();

    /**
     * Gets the list of registered decorated scalar types.
     *
     * @return the scalar types
     */
    Map<String, GraphQLScalarType> getScalarTypes();
}
