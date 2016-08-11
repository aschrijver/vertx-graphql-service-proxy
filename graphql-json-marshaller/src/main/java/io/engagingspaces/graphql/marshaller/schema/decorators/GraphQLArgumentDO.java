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

import graphql.schema.*;
import io.engagingspaces.graphql.marshaller.json.JsonReference;
import io.engagingspaces.graphql.marshaller.json.impl.JsonObjectHelper;
import io.engagingspaces.graphql.marshaller.json.PropNames;
import io.engagingspaces.graphql.marshaller.schema.SchemaChildDecorator;
import io.engagingspaces.graphql.marshaller.schema.SchemaContext;
import io.engagingspaces.graphql.marshaller.schema.SchemaDecorator;
import io.vertx.core.json.JsonObject;

import java.util.Collections;

/**
 * Data object wrapper for {@link GraphQLArgument}.
 *
 * @author <a href="https://github.com/aschrijver/">Arnold Schrijver</a>
 */
public class GraphQLArgumentDO extends GraphQLArgument
        implements SchemaChildDecorator<GraphQLArgument, SchemaDecorator> {

    private final GraphQLArgument argument;
    private final JsonObject argumentJson;
    private final SchemaDecorator parent;
    private final SchemaContext context;
    private final JsonReference jsonReference;

    private GraphQLArgumentDO(GraphQLArgument argument, JsonObject argumentJson,
                              SchemaDecorator parent, SchemaContext context) {
        super(SchemaContext.EMPTY, new GraphQLInputObjectType(SchemaContext.EMPTY, null, Collections.emptyList()));
        this.argument = argument;
        this.argumentJson = argumentJson;
        this.parent = parent;
        this.context = context;
        this.jsonReference = context.registerDecorator(this);
    }

    protected GraphQLArgumentDO(GraphQLArgument argument, SchemaContext context, SchemaDecorator parent) {
        this(argument, null, parent, context);
    }

    /**
     * Creates the GraphQL object from its json serialization data using the provided schema context.
     *
     * @param json    the json data
     * @param context the schema context
     * @param parent  the parent field
     */
    @SuppressWarnings("unused")
    public GraphQLArgumentDO(JsonObject json, SchemaContext context, GraphQLFieldDefinitionDO parent) {
        this(null, json, parent, context);
    }

    /**
     * Creates the GraphQL object from its json serialization data using the provided schema context.
     *
     * @param json    the json data
     * @param context the schema context
     * @param parent  the parent directive
     */
    @SuppressWarnings("unused")
    public GraphQLArgumentDO(JsonObject json, SchemaContext context, GraphQLDirectiveDO parent) {
        this(null, json, parent, context);
    }

    /**
     * Creates a decorator of the provided GraphQL object that turns it into a Vert.x data object.
     *
     * @param original the schema object to decorate
     * @param context  the schema context
     * @param parent   the parent directive or field definition
     * @return the schema object decorated as a vertx data object
     */
    public static GraphQLArgument create(
            GraphQLArgument original, SchemaContext context, SchemaDecorator parent) {
        return new GraphQLArgumentDO(original, context, parent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonObject toJson() {
        if (argumentJson != null) {
            return argumentJson;
        }
        return JsonObjectHelper.jsonObject()
                .put(PropNames.MARSHALED_TYPE, GraphQLArgument.class.getName())
                .put(PropNames.NAME, getName())
                .putIfPresent(PropNames.DESCRIPTION, getDescription())
                .put(PropNames.TYPE, context.referenceTo(getType()))
                .putIfPresent(PropNames.DEFAULT_VALUE, getDefaultValue());
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
    public SchemaDecorator getParent() {
        return parent;
    }

    @Override
    public String getName() {
        if (argument == null) {
            return argumentJson.getString(PropNames.NAME);
        }
        return argument.getName();
    }

    @Override
    public GraphQLInputType getType() {
        if (argument == null) {
            return context.unmarshall(argumentJson.getJsonObject(PropNames.TYPE));
        } else if (argument.getType() instanceof GraphQLList || argument.getType() instanceof GraphQLNonNull) {
            return context.decoratorOf(argument.getType(), this);
        }
        return context.decoratorOf(argument.getType(), this);
    }

    @Override
    public Object getDefaultValue() {
        if (argument == null) {
            return argumentJson.getValue(PropNames.DEFAULT_VALUE);
        }
        return argument.getDefaultValue();
    }

    @Override
    public String getDescription() {
        if (argument == null) {
            return argumentJson.getString(PropNames.DESCRIPTION);
        }
        return argument.getDescription();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraphQLArgument original() {
        return argument;
    }
}
