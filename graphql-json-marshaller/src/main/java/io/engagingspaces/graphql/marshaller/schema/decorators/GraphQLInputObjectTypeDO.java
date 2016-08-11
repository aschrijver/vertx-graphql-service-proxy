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

import graphql.schema.GraphQLInputObjectField;
import graphql.schema.GraphQLInputObjectType;
import io.engagingspaces.graphql.marshaller.json.JsonReference;
import io.engagingspaces.graphql.marshaller.json.impl.JsonObjectHelper;
import io.engagingspaces.graphql.marshaller.json.PropNames;
import io.engagingspaces.graphql.marshaller.schema.SchemaContext;
import io.engagingspaces.graphql.marshaller.schema.SchemaDecorator;
import io.vertx.core.json.JsonObject;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Data object wrapper for {@link GraphQLInputObjectType}.
 *
 * @author <a href="https://github.com/aschrijver/">Arnold Schrijver</a>
 */
public class GraphQLInputObjectTypeDO extends GraphQLInputObjectType
        implements SchemaDecorator<GraphQLInputObjectType> {

    private final GraphQLInputObjectType type;
    private final JsonObject typeJson;
    private final SchemaContext context;
    private final JsonReference jsonReference;

    private GraphQLInputObjectTypeDO(GraphQLInputObjectType type, JsonObject typeJson, SchemaContext context) {
        super(SchemaContext.EMPTY, null, Collections.emptyList());
        this.type = type;
        this.typeJson = typeJson;
        this.context = context;
        this.jsonReference = context.registerDecorator(this);
    }

    protected GraphQLInputObjectTypeDO(GraphQLInputObjectType type, SchemaContext context) {
        this(type, null, context);
    }

    /**
     * Creates the GraphQL object from its json serialization data using the provided schema context.
     *
     * @param json    the json data
     * @param context the schema context
     */
    @SuppressWarnings("unused")
    public GraphQLInputObjectTypeDO(JsonObject json, SchemaContext context) {
        this(null, json, context);
    }

    /**
     * Creates a decorator of the provided GraphQL object that turns it into a Vert.x data object.
     *
     * @param original the schema object to decorate
     * @param context  the schema context
     * @return the schema object decorated as a vertx data object
     */
    public static GraphQLInputObjectType create(GraphQLInputObjectType original, SchemaContext context) {
        return new GraphQLInputObjectTypeDO(original, context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonObject toJson() {
        if (typeJson != null) {
            return typeJson;
        }
        return JsonObjectHelper.jsonObject()
                .put(PropNames.MARSHALED_TYPE, GraphQLInputObjectType.class.getName())
                .put(PropNames.NAME, getName())
                .putIfPresent(PropNames.DESCRIPTION, getDescription())
                .put(PropNames.FIELDS, getFields().stream()
                        .map(context::marshall)
                        .collect(Collectors.toList()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonReference jsonReference() {
        return jsonReference;
    }

    @Override
    public String getName() {
        if (type == null) {
            return typeJson.getString(PropNames.NAME);
        }
        return type.getName();
    }

    @Override
    public String getDescription() {
        if (type == null) {
            return typeJson.getString(PropNames.DESCRIPTION);
        }
        return type.getDescription();
    }

    @Override
    public List<GraphQLInputObjectField> getFields() {
        if (type == null) {
            return context.unmarshallList(typeJson, PropNames.FIELDS, this);
        }
        return type.getFields().stream()
                .map(context::decoratorOf)
                .collect(Collectors.toList());
    }

    @Override
    public GraphQLInputObjectField getField(String name) {
        if (type == null) {
            return (GraphQLInputObjectField) context.unmarshall(
                    typeJson.getJsonObject(PropNames.FIELDS, new JsonObject()).getJsonObject(name), this);
        }
        return context.decoratorOf(type.getField(name));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraphQLInputObjectType original() {
        return type;
    }
}
