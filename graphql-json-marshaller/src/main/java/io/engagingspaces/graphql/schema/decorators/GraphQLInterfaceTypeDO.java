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

package io.engagingspaces.graphql.schema.decorators;

import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInterfaceType;
import graphql.schema.TypeResolver;
import io.engagingspaces.graphql.json.JsonReference;
import io.engagingspaces.graphql.json.impl.JsonObjectHelper;
import io.engagingspaces.graphql.schema.SchemaContext;
import io.engagingspaces.graphql.schema.SchemaDecorator;
import io.vertx.core.json.JsonObject;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static io.engagingspaces.graphql.json.PropNames.*;
import static io.engagingspaces.graphql.schema.SchemaContext.EMPTY;

/**
 * Data object wrapper for {@link GraphQLInterfaceType}.
 *
 * @author <a href="https://github.com/aschrijver/">Arnold Schrijver</a>
 */
public class GraphQLInterfaceTypeDO extends GraphQLInterfaceType implements SchemaDecorator<GraphQLInterfaceType> {

    private final GraphQLInterfaceType type;
    private final JsonObject typeJson;
    private final SchemaContext context;
    private final JsonReference jsonReference;
    private List<GraphQLFieldDefinition> fields;

    private GraphQLInterfaceTypeDO(GraphQLInterfaceType type, JsonObject typeJson, SchemaContext context) {
        super(EMPTY, null, Collections.emptyList(), (t) -> null);
        this.type = type;
        this.typeJson = typeJson;
        this.context = context;
        this.jsonReference = context.registerDecorator(this);
    }

    protected GraphQLInterfaceTypeDO(GraphQLInterfaceType type, SchemaContext context) {
        this(type, null, context);
    }

    /**
     * Creates the GraphQL object from its json serialization data using the provided schema context.
     *
     * @param json    the json data
     * @param context the schema context
     */
    @SuppressWarnings("unused")
    public GraphQLInterfaceTypeDO(JsonObject json, SchemaContext context) {
        this(null, json, context);
        context.unmarshall(json.getJsonObject(TYPE_RESOLVER), this);
        fields = context.unmarshallList(json, FIELD_DEFINITIONS, this);
    }

    /**
     * Creates a decorator of the provided GraphQL object that turns it into a Vert.x data object.
     *
     * @param original the schema object to decorate
     * @param context  the schema context
     * @return the schema object decorated as a vertx data object
     */
    public static GraphQLInterfaceType create(GraphQLInterfaceType original, SchemaContext context) {
        return new GraphQLInterfaceTypeDO(original, context);
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
                .put(MARSHALED_TYPE, GraphQLInterfaceType.class.getName())
                .put(NAME, getName())
                .putIfPresent(DESCRIPTION, getDescription())
                .putIfPresent(TYPE_RESOLVER, context.referenceTo(getTypeResolver()))
                .put(FIELD_DEFINITIONS, getFieldDefinitions().stream()
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
    public GraphQLFieldDefinition getFieldDefinition(String name) {
        Objects.requireNonNull(name, "Field name cannot be null");
        if (type == null) {
            return  fields.stream().filter(field -> name.equals(field.getName())).findAny().get();
        }
        return context.decoratorOf(type.getFieldDefinition(name), this);
    }

    @Override
    public List<GraphQLFieldDefinition> getFieldDefinitions() {
        if (type == null) {
            return fields;
        }
        return type.getFieldDefinitions().stream()
                .map(field -> context.decoratorOf(field, this))
                .collect(Collectors.toList());
    }

    @Override
    public String getName() {
        if (type == null) {
            return typeJson.getString(NAME);
        }
        return type.getName();
    }

    @Override
    public String getDescription() {
        if (type == null) {
            return typeJson.getString(DESCRIPTION);
        }
        return type.getDescription();
    }

    @Override
    public TypeResolver getTypeResolver() {
        if (type == null) {
            return context.unmarshall(typeJson.getJsonObject(TYPE_RESOLVER), this);
        }
        return context.decoratorOf(type.getTypeResolver(), this);
    }

    @Override
    public String toString() {
        if (type == null) {
            return jsonReference.toString();
        }
        return type.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraphQLInterfaceType original() {
        return type;
    }
}

