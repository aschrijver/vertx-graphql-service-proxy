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

import graphql.schema.Coercing;
import graphql.schema.GraphQLEnumType;
import graphql.schema.GraphQLEnumValueDefinition;
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
 * Data object wrapper for {@link GraphQLEnumType}.
 *
 * @author <a href="https://github.com/aschrijver/">Arnold Schrijver</a>
 */
public class GraphQLEnumTypeDO extends GraphQLEnumType implements SchemaDecorator<GraphQLEnumType> {

    private final GraphQLEnumType enumType;
    private final JsonObject enumTypeJson;
    private final SchemaContext context;
    private final JsonReference jsonReference;

    private GraphQLEnumTypeDO(GraphQLEnumType enumType, JsonObject enumTypeJson, SchemaContext context) {
        super(SchemaContext.EMPTY, null, Collections.emptyList());
        this.enumType = enumType;
        this.enumTypeJson = enumTypeJson;
        this.context = context;
        this.jsonReference = context.registerDecorator(this);
    }

    /**
     * Protected constructor used to wrap the provided GraphQL object.
     *
     * @param enumType the GraphQL object
     * @param context  the schema context
     */
    protected GraphQLEnumTypeDO(GraphQLEnumType enumType, SchemaContext context) {
        this(enumType, null, context);
    }

    /**
     * Creates the GraphQL object from its json serialization data using the provided schema context.
     *
     * @param json    the json data
     * @param context the schema context
     */
    @SuppressWarnings("unused")
    public GraphQLEnumTypeDO(JsonObject json, SchemaContext context) {
        this(null, json, context);
    }

    /**
     * Creates a decorator of the provided GraphQL object that turns it into a Vert.x data object.
     *
     * @param original the schema object to decorate
     * @param context  the schema context
     * @return the schema object decorated as a vertx data object
     */
    public static GraphQLEnumType create(GraphQLEnumType original, SchemaContext context) {
        return new GraphQLEnumTypeDO(original, context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonObject toJson() {
        if (enumTypeJson != null) {
            return enumTypeJson;
        }
        return JsonObjectHelper.jsonObject()
                .put(PropNames.MARSHALED_TYPE, GraphQLEnumType.class.getName())
                .put(PropNames.NAME, getName())
                .putIfPresent(PropNames.DESCRIPTION, getDescription())
                .put(PropNames.VALUES, getValues().stream()
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
    public List<GraphQLEnumValueDefinition> getValues() {
        if (enumType == null) {
            return context.unmarshallList(enumTypeJson, PropNames.VALUES, this);
        }
        return enumType.getValues().stream()
                .map(value -> context.decoratorOf(value, this))
                .collect(Collectors.toList());
    }

    @Override
    public String getName() {
        if (enumType == null) {
            return enumTypeJson.getString(PropNames.NAME);
        }
        return enumType.getName();
    }

    @Override
    public String getDescription() {
        if (enumType == null) {
            return enumTypeJson.getString(PropNames.DESCRIPTION);
        }
        return enumType.getDescription();
    }

    @Override
    public Coercing getCoercing() {
        if (enumType == null) {
            // TODO
            return null;
        }
        return enumType.getCoercing();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraphQLEnumType original() {
        return enumType;
    }
}
