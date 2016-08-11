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

package io.engagingspaces.graphql.marshaller.schema.decorators;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.StaticDataFetcher;
import io.engagingspaces.graphql.marshaller.json.JsonReference;
import io.engagingspaces.graphql.marshaller.json.JsonSerializable;
import io.engagingspaces.graphql.marshaller.json.impl.JsonObjectHelper;
import io.engagingspaces.graphql.marshaller.schema.SchemaChildDecorator;
import io.engagingspaces.graphql.marshaller.schema.SchemaContext;
import io.vertx.core.json.JsonObject;

import java.util.UUID;

import static io.engagingspaces.graphql.marshaller.json.JsonReference.REF_KEY;

/**
 * Data object for {@link DataFetcher}.
 *
 * @author <a href="https://github.com/aschrijver/">Arnold Schrijver</a>
 */
public class DataFetcherDO implements DataFetcher, SchemaChildDecorator<DataFetcher, GraphQLFieldDefinition> {

    private final String id;
    private final DataFetcher dataFetcher;
    private final JsonObject dataFetcherJson;
    private final GraphQLFieldDefinition parent;
    private final SchemaContext context;
    private final JsonReference jsonReference;
    private final JsonObject staticValue;

    private DataFetcherDO(DataFetcher dataFetcher, JsonObject dataFetcherJson,
                         GraphQLFieldDefinition parent, SchemaContext schemaContext) {
        this.dataFetcherJson = dataFetcherJson;
        this.parent = parent;
        this.context = schemaContext;
        this.id = dataFetcherJson == null ? UUID.randomUUID().toString() : dataFetcherJson.getString(ID);
        if (dataFetcher == null) {
            if (id != null && context.getDataFetchers().containsKey(id)) {
                this.dataFetcher = context.getDataFetchers().get(id);
            } else {
                this.dataFetcher = (environment -> null);
            }
        } else {
            this.dataFetcher = dataFetcher;
        }
        if (dataFetcherJson == null) {
            this.staticValue = dataFetcher instanceof StaticDataFetcher ?
                    new JsonObject().put(STATIC_VALUE, dataFetcher.get(null)) : null;
        } else {
            this.staticValue = dataFetcherJson.getJsonObject(STATIC_VALUE);
        }
        this.jsonReference = context.registerDataFetcher(this);
    }

    protected DataFetcherDO(DataFetcher dataFetcher, SchemaContext context, GraphQLFieldDefinition parent) {
        this(dataFetcher, null, parent, context);
    }

    /**
     * Creates the GraphQL object from its json serialization data using the provided schema context.
     *
     * @param json    the json data
     * @param context the schema context
     * @param parent  the parent field definition
     */
    @SuppressWarnings("unused")
    public DataFetcherDO(JsonObject json, SchemaContext context, GraphQLFieldDefinitionDO parent) {
        this(null, json, parent, context);
    }

    /**
     * Creates a decorator of the provided GraphQL data fetcher that turns it into a Vert.x data object.
     *
     * @param dataFetcher the data fetcher
     * @param context     the schema context
     * @param parent      the parent field
     * @return the data fetcher decorated as a vertx data object
     */
    public static DataFetcher create(DataFetcher dataFetcher, SchemaContext context, GraphQLFieldDefinition parent) {
        return new DataFetcherDO(dataFetcher, context, parent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonObject toJson() {
        if (dataFetcherJson != null) {
            return dataFetcherJson;
        }
        String marshaled = dataFetcher instanceof StaticDataFetcher ?
                StaticDataFetcher.class.getName() : DataFetcher.class.getName();
        return JsonObjectHelper.jsonObject()
                .put(MARSHALED_TYPE, marshaled)
                .put(MARSHALED_TYPE_CLASS, dataFetcher.getClass().getName())
                .putIfPresent(PARENT, new JsonObject().put(REF_KEY,
                        ((JsonSerializable) parent).jsonReference().getReference()))
                .put(ID, getId())
                .putIf(STATIC_VALUE, staticValue, val -> val != null, val -> val.getJsonObject(STATIC_VALUE));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonReference jsonReference() {
        return jsonReference;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraphQLFieldDefinition getParent() {
        return parent;
    }

    /**
     * Gets the ID of the data fetcher that is serialized to Json.
     *
     * @return the id of the data fetcher
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the static value of the data fetcher.
     * <p>
     * This methods holds the json-serialized value of the data fetcher if it is of type {@link StaticDataFetcher}.
     *
     * @return the static value, or null
     */
    public JsonObject getStaticValue() {
        return staticValue;
    }

    @Override
    public Object get(DataFetchingEnvironment environment) {
        return dataFetcher.get(environment);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataFetcher original() {
        return dataFetcher;
    }
}
