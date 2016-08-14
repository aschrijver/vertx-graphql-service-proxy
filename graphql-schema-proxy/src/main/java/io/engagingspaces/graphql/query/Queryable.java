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

package io.engagingspaces.graphql.query;

import io.engagingspaces.graphql.schema.SchemaMetadata;
import io.vertx.codegen.annotations.ProxyClose;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.serviceproxy.ProxyHelper;

/**
 * Service proxy interface that provides access to the schema definitions that are exposed by a GraphQL publisher.
 *
 * @author <a href="https://github.com/aschrijver/">Arnold Schrijver</a>
 */
@ProxyGen
public interface Queryable {

    /**
     * Name of the discovery service type for GraphQL schema's.
     */
    String SERVICE_TYPE = "graphql-service";

    /**
     * The prefix that is combined with the root query name of the associated GraphQL schema
     * to form the endpoint address used in service discovery.
     */
    String ADDRESS_PREFIX = "service.graphql";

    /**
     * Creates a service proxy to the {@link Queryable} implementation at the specified address.
     * <p>
     * The returned result can either be a client-side {@link io.engagingspaces.graphql.proxy.GraphQLSchemaProxy},
     * or a regular Vert.x service proxy that delegates its calls to a remote GraphQL service implementation over
     * the event bus. Return type is determined by the schema metadata that is passed in.
     *
     * @param vertx          the vert.x instance
     * @param address        the address of the service proxy
     * @param schemaMetadata the schema-related metadata to pass to the proxy
     * @return the graphql service proxy
     */
    static Queryable createProxy(Vertx vertx, String address, JsonObject schemaMetadata) {
        SchemaMetadata metadata = new SchemaMetadata(schemaMetadata);
        if (metadata.isClientProxy()) {
            return metadata.getClientProxy(vertx);
        }
        return ProxyHelper.createProxy(Queryable.class, vertx, address, metadata.options().getDeliveryOptions());
    }

    /**
     * Executes the GraphQL query on the GraphQL schema proxy.
     * <p>
     * On success a {@link QueryResult} is returned. While this indicates the query executor has finished processing,
     * the query itself might still have failed, so be sure to check {@link QueryResult#isSucceeded()} and
     * {@link QueryResult#getErrors()} properties on the query result afterwards.
     * afterwards.
     *
     * @param graphqlQuery  the graphql query
     * @param resultHandler the result handler with the query result on success, or a failure
     */
    void query(String graphqlQuery, Handler<AsyncResult<QueryResult>> resultHandler);

    /**
     * Executes the GraphQL query on the GraphQL schema proxy using the provided variables.
     *
     * @param graphqlQuery  the graphql query
     * @param variables     the query variables
     * @param resultHandler the result handler with the graphql query result on success, or a failure
     */
    void queryWithVariables(String graphqlQuery, JsonObject variables,
                            Handler<AsyncResult<QueryResult>> resultHandler);


    /**
     * Resolve the type that is indicated by the {@code typeHolder} parameter, using the type resolver with the
     * provided ID.
     * <p>
     * This method must be overridden by implementers, and is only applicable for schema definition proxies of type
     * {@link io.engagingspaces.graphql.schema.SchemaProxyType#ProxyClient}.
     * <p>
     * The return json object is the serialized {@link io.engagingspaces.graphql.marshaller.json.JsonReference} to
     * the resolved graphql schema object type.
     *
     * @param typeResolverId the ID of the type resolver
     * @param typeHolder     the holder of the type value to resolve to a schema type
     * @param resultHandler  the result handler
     * @throws IllegalStateException if invoked directly on this interface rather than a valid implementation
     */
    default void resolveType(String typeResolverId, JsonObject typeHolder,
                             Handler<AsyncResult<JsonObject>> resultHandler) {
        throw new IllegalStateException("Method must be overridden in a sub-class");
    }

    /**
     * Fetch the data from the data fetcher with the provided ID.
     * <p>
     * This method must be overridden by implementers, and is only applicable for schema definition proxies of type
     * {@link io.engagingspaces.graphql.schema.SchemaProxyType#ProxyClient}.
     * <p>
     * The {@code dataFetchingEnvironment} parameter contains a json representation of the
     * {@link graphql.schema.DataFetchingEnvironment} that was passed at the consumer side when
     * invoking the data fetcher.
     * <p>
     * The returned json object is the serialized result of the data fetcher.
     *
     * @param dataFetcherId           the ID of the data fetcher
     * @param dataFetchingEnvironment the json representation of the data fetching environment
     * @param resultHandler           the result handler
     * @throws IllegalStateException if invoked directly on this interface rather than a valid implementation
     */
    default void fetchData(String dataFetcherId, JsonObject dataFetchingEnvironment,
                          Handler<AsyncResult<JsonObject>> resultHandler) {
        throw new IllegalStateException("Method must be overridden in a sub-class");
    }

    /**
     * Invoked when the queryable service proxy closes. Does nothing by default, but can be overridden in sub-classes.
     */
    @ProxyClose
    default void close() {
        // NO OP
    }
}
