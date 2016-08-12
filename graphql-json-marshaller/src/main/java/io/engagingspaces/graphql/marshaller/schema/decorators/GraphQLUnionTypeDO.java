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

import graphql.schema.GraphQLType;
import graphql.schema.GraphQLUnionType;
import graphql.schema.TypeResolver;
import io.engagingspaces.graphql.marshaller.json.JsonReference;
import io.engagingspaces.graphql.marshaller.json.impl.JsonObjectHelper;
import io.engagingspaces.graphql.marshaller.schema.SchemaContext;
import io.engagingspaces.graphql.marshaller.schema.SchemaDecorator;
import io.vertx.core.json.JsonObject;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static io.engagingspaces.graphql.marshaller.json.PropNames.*;
import static io.engagingspaces.graphql.marshaller.schema.SchemaContext.EMPTY;

/**
 * Data object wrapper for {@link GraphQLUnionType}.
 *
 * @author <a href="https://github.com/aschrijver/">Arnold Schrijver</a>
 */
public class GraphQLUnionTypeDO extends GraphQLUnionType
        implements SchemaDecorator<GraphQLUnionType> {

    private final GraphQLUnionType unionType;
    private final JsonObject unionTypeJson;
    private final  SchemaContext context;
    private final JsonReference jsonReference;

    private GraphQLUnionTypeDO(GraphQLUnionType unionType, JsonObject unionTypeJson, SchemaContext context) {
        super(EMPTY, null, Collections.emptyList(), (t) -> null);
        this.unionType = unionType;
        this.unionTypeJson = unionTypeJson;
        this.context = context;
        this.jsonReference = context.registerDecorator(this);
    }

    protected GraphQLUnionTypeDO(GraphQLUnionType unionType, SchemaContext context) {
        this(unionType, null, context);
    }

    /**
     * Creates the GraphQL object from its json serialization data using the provided schema context.
     *
     * @param json    the json data
     * @param context the schema context
     */
    @SuppressWarnings("unused")
    public GraphQLUnionTypeDO(JsonObject json, SchemaContext context) {
        this(null, json, context);
        context.unmarshall(json.getJsonObject(TYPE_RESOLVER), this);
        context.unmarshallList(json, TYPES);
    }

    /**
     * Creates a decorator of the provided GraphQL object that turns it into a Vert.x data object.
     *
     * @param original the schema object to decorate
     * @param context  the schema context
     * @return the schema object decorated as a vertx data object
     */
    public static GraphQLUnionType create(GraphQLUnionType original, SchemaContext context) {
        return new GraphQLUnionTypeDO(original, context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonObject toJson() {
        if (unionTypeJson != null) {
            return unionTypeJson;
        }
        return JsonObjectHelper.jsonObject()
                .put(MARSHALED_TYPE, GraphQLUnionType.class.getName())
                .put(NAME, getName())
                .putIfPresent(DESCRIPTION, getDescription())
                .put(TYPES, getTypes().stream()
                    .map(context::referenceTo)
                    .collect(Collectors.toList()))
                .putIfPresent(TYPE_RESOLVER, context.referenceTo(getTypeResolver()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonReference jsonReference() {
        return jsonReference;
    }

    public List<GraphQLType> getTypes() {
        if (unionType == null) {
            return context.unmarshallList(unionTypeJson, TYPES);
        }
        return unionType.getTypes().stream()
                .map(context::decoratorOf)
                .collect(Collectors.toList());
    }

    public TypeResolver getTypeResolver() {
        if (unionType == null) {
            return context.unmarshall(unionTypeJson.getJsonObject(TYPE_RESOLVER), this);
        }
        return context.decoratorOf(unionType.getTypeResolver(), this);
    }

    public String getName() {
        if (unionType == null) {
            return unionTypeJson.getString(NAME);
        }
        return unionType.getName();
    }

    public String getDescription() {
        if (unionType == null) {
            return unionTypeJson.getString(NAME);
        }
        return unionType.getDescription();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraphQLUnionType original() {
        return unionType;
    }
}
