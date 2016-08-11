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

import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInterfaceType;
import graphql.schema.GraphQLObjectType;
import io.engagingspaces.graphql.marshaller.json.JsonReference;
import io.engagingspaces.graphql.marshaller.json.impl.JsonObjectHelper;
import io.engagingspaces.graphql.marshaller.json.PropNames;
import io.engagingspaces.graphql.marshaller.schema.SchemaContext;
import io.engagingspaces.graphql.marshaller.schema.SchemaDecorator;
import io.vertx.core.json.JsonObject;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Data object wrapper for {@link GraphQLObjectType}.
 *
 * @author <a href="https://github.com/aschrijver/">Arnold Schrijver</a>
 */
public class GraphQLObjectTypeDO extends GraphQLObjectType
        implements SchemaDecorator<GraphQLObjectType> {

    private final GraphQLObjectType objectType;
    private final JsonObject objectTypeJson;
    private final SchemaContext context;
    private final JsonReference jsonReference;
    private List<GraphQLFieldDefinition> fields;

    private GraphQLObjectTypeDO(GraphQLObjectType objectType, JsonObject objectTypeJson, SchemaContext context) {
        super(SchemaContext.EMPTY, null, Collections.emptyList(), Collections.emptyList());
        this.objectType = objectType;
        this.objectTypeJson = objectTypeJson;
        this.context = context;
        this.jsonReference = context.registerDecorator(this);
    }

    protected GraphQLObjectTypeDO(GraphQLObjectType objectType, SchemaContext context) {
        this(objectType, null, context);
    }

    /**
     * Creates the GraphQL object from its json serialization data using the provided schema context.
     *
     * @param json    the json data
     * @param context the schema context
     */
    @SuppressWarnings("unused")
    public GraphQLObjectTypeDO(JsonObject json, SchemaContext context) {
        this(null, json, context);
        this.fields = context.unmarshallList(json, PropNames.FIELD_DEFINITIONS, this);
    }

    /**
     * Creates a decorator of the provided GraphQL object that turns it into a Vert.x data object.
     *
     * @param original the schema object to decorate
     * @param context  the schema context
     * @return the schema object decorated as a vertx data object
     */
    public static GraphQLObjectType create(GraphQLObjectType original, SchemaContext context) {
        return new GraphQLObjectTypeDO(original, context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonObject toJson() {
        if (objectTypeJson != null) {
            return objectTypeJson;
        }
        return JsonObjectHelper.jsonObject()
                .put(PropNames.MARSHALED_TYPE, GraphQLObjectType.class.getName())
                .put(PropNames.NAME, getName())
                .putIfPresent(PropNames.DESCRIPTION, getDescription())
                .put(PropNames.FIELD_DEFINITIONS, getFieldDefinitions().stream()
                        .map(context::marshall)
                        .collect(Collectors.toList()))
                .putIfPresent(PropNames.INTERFACES, getInterfaces().stream()
                        .map(context::referenceTo)
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
        if (objectType == null) {
            return  fields.stream().filter(field -> name.equals(field.getName())).findAny().get();
        }
        return context.decoratorOf(objectType.getFieldDefinition(name), this);
    }

    @Override
    public List<GraphQLFieldDefinition> getFieldDefinitions() {
        if (objectType == null) {
            return fields;
        }
        return objectType.getFieldDefinitions().stream()
                .map(definition -> context.decoratorOf(definition, this))
                .collect(Collectors.toList());
    }

    @Override
    public List<GraphQLInterfaceType> getInterfaces() {
        if (objectType == null) {
            return context.unmarshallList(objectTypeJson, PropNames.INTERFACES);
        }
        return objectType.getInterfaces().stream()
                .map(iface -> context.decoratorOf(iface, this))
                .collect(Collectors.toList());
    }

    @Override
    public String getDescription() {
        if (objectType == null) {
            return objectTypeJson.getString(PropNames.DESCRIPTION);
        }
        return objectType.getDescription();
    }

    @Override
    public String getName() {
        if (objectType == null) {
            return objectTypeJson.getString(PropNames.NAME);
        }
        return objectType.getName();
    }

    @Override
    public String toString() {
        if (objectType == null) {
            return jsonReference.getReference();
        }
        return objectType.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraphQLObjectType original() {
        return objectType;
    }
}
