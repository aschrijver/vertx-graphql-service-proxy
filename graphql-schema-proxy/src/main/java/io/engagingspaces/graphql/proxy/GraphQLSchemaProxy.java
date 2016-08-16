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

package io.engagingspaces.graphql.proxy;

import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import io.engagingspaces.graphql.marshaller.schema.SchemaContext;
import io.engagingspaces.graphql.marshaller.schema.decorators.GraphQLSchemaDO;
import io.engagingspaces.graphql.query.QueryResult;
import io.engagingspaces.graphql.query.Queryable;
import io.engagingspaces.graphql.schema.SchemaDefinition;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

/**
 * Data object wrapper for {@link GraphQLSchema}.
 *
 * @author <a href="https://github.com/aschrijver/">Arnold Schrijver</a>
 */
public class GraphQLSchemaProxy extends GraphQLSchemaDO implements Queryable {

    private Vertx vertx;
    private SchemaContext schemaContext;

    /**
     * Protected constructor used to wrap the provided GraphQL object.
     *
     * @param vertx   the vertx instance
     * @param schema  the graphql schema object
     * @param context the schema context
     */
    protected GraphQLSchemaProxy(Vertx vertx, GraphQLSchema schema, SchemaContext context) {
        super(schema, context);
        this.vertx = vertx;
    }

    /**
     * Creates the GraphQL object from its json serialization data.
     *
     * @param vertx   the vertx instance
     * @param json    the json data
     * @param context the schema context
     */
    public GraphQLSchemaProxy(Vertx vertx, JsonObject json, SchemaContext context) {
        super(json, context);
        this.vertx = vertx;
    }

    @Override
    public void query(String graphqlQuery, Handler<AsyncResult<QueryResult>> resultHandler) {
        queryWithVariables(graphqlQuery, null, resultHandler);
    }

    @Override
    public void queryWithVariables(String graphqlQuery, JsonObject variables,
                                   Handler<AsyncResult<QueryResult>> resultHandler) {
        try {
            QueryResult result = queryBlocking(graphqlQuery, variables);
            resultHandler.handle(Future.succeededFuture(result));
        } catch (RuntimeException ex) {
            resultHandler.handle(Future.failedFuture(ex));
        }
    }

    /**
     * Executes a blocking call to the GraphQL query processor and executes the query.
     *
     * @param graphqlQuery the graphql query
     * @param variables    the variables to pass to the query
     * @return the graphql query result
     */
    public QueryResult queryBlocking(String graphqlQuery, JsonObject variables) {
        Objects.requireNonNull(graphqlQuery, "GraphQL query cannot be null");
        GraphQL graphQL = new GraphQL(this);
        ExecutionResult result;
        if (variables == null) {
            result = graphQL.execute(graphqlQuery);
        } else {
            result = graphQL.execute(graphqlQuery, (Object) null, variables.getMap());
        }
        return SchemaDefinition.convertToQueryResult(result);
    }

    @Override
    public void resolveType(String typeResolverId, JsonObject typeHolder,
                            Handler<AsyncResult<JsonObject>> resultHandler) {
        Objects.requireNonNull(typeResolverId, "Type resolver id cannot be null");
        Objects.requireNonNull(typeHolder, "Type value holder cannot be null");
    }

    @Override
    public void fetchData(String dataFetcherId, JsonObject dataFetchingEnvironment,
                          Handler<AsyncResult<JsonObject>> resultHandler) {
        Objects.requireNonNull(dataFetcherId, "Data fetcher id cannot be null");
        Objects.requireNonNull(dataFetchingEnvironment, "Data fetching environment json cannot be null");
    }
}
