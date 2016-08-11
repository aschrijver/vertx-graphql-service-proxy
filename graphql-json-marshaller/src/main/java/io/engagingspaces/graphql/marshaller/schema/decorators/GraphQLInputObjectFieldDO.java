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

import graphql.schema.GraphQLEnumType;
import graphql.schema.GraphQLInputObjectField;
import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLInputType;
import io.engagingspaces.graphql.marshaller.json.JsonReference;
import io.engagingspaces.graphql.marshaller.json.impl.JsonObjectHelper;
import io.engagingspaces.graphql.marshaller.json.PropNames;
import io.engagingspaces.graphql.marshaller.schema.SchemaChildDecorator;
import io.engagingspaces.graphql.marshaller.schema.SchemaContext;
import io.vertx.core.json.JsonObject;

import java.util.Collections;

/**
 * Data object wrapper for {@link GraphQLInputObjectField}.
 *
 * @author <a href="https://github.com/aschrijver/">Arnold Schrijver</a>
 */
public class GraphQLInputObjectFieldDO extends GraphQLInputObjectField
        implements SchemaChildDecorator<GraphQLInputObjectField, GraphQLInputObjectType> {

    private final GraphQLInputObjectField field;
    private final JsonObject fieldJson;
    private final GraphQLInputObjectType parent;
    private final SchemaContext context;
    private final JsonReference jsonReference;

    private GraphQLInputObjectFieldDO(GraphQLInputObjectField field, JsonObject fieldJson,
                                     GraphQLInputObjectType parent, SchemaContext context) {
        super(SchemaContext.EMPTY, null, new GraphQLEnumType(SchemaContext.EMPTY, null, Collections.emptyList()), null);
        this.field = field;
        this.fieldJson = fieldJson;
        this.parent = parent;
        this.context = context;
        this.jsonReference = context.registerDecorator(this);
    }

    protected GraphQLInputObjectFieldDO(
            GraphQLInputObjectField field, SchemaContext context, GraphQLInputObjectType parent) {
        this(field, null, parent, context);
    }

    /**
     * Creates the GraphQL object from its json serialization data using the provided schema context.
     *
     * @param json    the json data
     * @param context the schema context
     */
    @SuppressWarnings("unused")
    public GraphQLInputObjectFieldDO(JsonObject json, SchemaContext context, GraphQLInputObjectTypeDO parent) {
        this(null, json, parent, context);
    }

    /**
     * Creates a decorator of the provided GraphQL object that turns it into a Vert.x data object.
     *
     * @param original the schema object to decorate
     * @param context  the schema context
     * @param parent   the parent input object type
     * @return the schema object decorated as a vertx data object
     */
    public static GraphQLInputObjectField create(
            GraphQLInputObjectField original, SchemaContext context, GraphQLInputObjectType parent) {
        return new GraphQLInputObjectFieldDO(original, context, parent);
    }

    /**
     * {@inheritDoc}
     */
    public JsonObject toJson() {
        if (fieldJson != null) {
            return fieldJson;
        }
        return JsonObjectHelper.jsonObject()
                .put(PropNames.MARSHALED_TYPE, GraphQLInputObjectField.class.getName())
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
    public GraphQLInputObjectType getParent() {
        return parent;
    }

    @Override
    public String getName() {
        if (field == null) {
            return fieldJson.getString(PropNames.NAME);
        }
        return field.getName();
    }

    @Override
    public GraphQLInputType getType() {
        if (field == null) {
            return context.unmarshall(fieldJson.getJsonObject(PropNames.TYPE));
        }
        return context.decoratorOf(field.getType());
    }

    @Override
    public Object getDefaultValue() {
        if (field == null) {
            return fieldJson.getValue(PropNames.DEFAULT_VALUE);
        }
        return field.getDefaultValue();
    }

    @Override
    public String getDescription() {
        if (field == null) {
            return fieldJson.getString(PropNames.DESCRIPTION);
        }
        return field.getDescription();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraphQLInputObjectField original() {
        return field;
    }
}
