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

import graphql.Scalars;
import graphql.schema.Coercing;
import graphql.schema.GraphQLScalarType;
import io.engagingspaces.graphql.marshaller.json.JsonReference;
import io.engagingspaces.graphql.marshaller.json.impl.JsonObjectHelper;
import io.engagingspaces.graphql.marshaller.schema.SchemaContext;
import io.engagingspaces.graphql.marshaller.schema.SchemaDecorator;
import io.vertx.core.json.JsonObject;

import static io.engagingspaces.graphql.marshaller.json.PropNames.*;
import static io.engagingspaces.graphql.marshaller.schema.SchemaContext.EMPTY;

/**
 * Data object wrapper for {@link GraphQLScalarType}.
 *
 * @author <a href="https://github.com/aschrijver/">Arnold Schrijver</a>
 */
public class GraphQLScalarTypeDO extends GraphQLScalarType
        implements SchemaDecorator<GraphQLScalarType> {

    private final GraphQLScalarType scalarType;
    private final JsonObject scalarTypeJson;
    private final SchemaContext context;
    private final JsonReference jsonReference;

    private GraphQLScalarTypeDO(GraphQLScalarType scalar, JsonObject scalarTypeJson, SchemaContext context) {
        super(EMPTY, null, Scalars.GraphQLBoolean.getCoercing());
        this.scalarTypeJson = scalarTypeJson;
        if (scalar == null) {
            switch (getName()) {
                case "String":
                    scalarType = Scalars.GraphQLString;
                    break;
                case "Boolean":
                    scalarType = Scalars.GraphQLBoolean;
                    break;
                case "Int":
                    scalarType = Scalars.GraphQLInt;
                    break;
                case "Float":
                    scalarType = Scalars.GraphQLFloat;
                    break;
                case "Long":
                    scalarType = Scalars.GraphQLLong;
                    break;
                case "ID":
                    scalarType = Scalars.GraphQLID;
                    break;
                default:
                    scalarType = null;
            }
        } else {
            this.scalarType = scalar;
        }
        this.context = context;
        this.jsonReference = this.context.registerScalarType(this);
    }

    protected GraphQLScalarTypeDO(GraphQLScalarType scalarType, SchemaContext context) {
        this(scalarType, null, context);
    }

    /**
     * Creates the GraphQL object from its json serialization data using the provided schema context.
     *
     * @param json    the json data
     * @param context the schema context
     */
    @SuppressWarnings("unused")
    public GraphQLScalarTypeDO(JsonObject json, SchemaContext context) {
        this(null, json, context);
    }

    /**
     * Creates a decorator of the provided GraphQL object that turns it into a Vert.x data object.
     *
     * @param original the schema object to decorate
     * @param context  the schema context
     * @return the schema object decorated as a vertx data object
     */
    public static GraphQLScalarType create(GraphQLScalarType original, SchemaContext context) {
        return new GraphQLScalarTypeDO(original, context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonObject toJson() {
        if (scalarTypeJson != null) {
            return scalarTypeJson;
        }
        return JsonObjectHelper.jsonObject()
                .put(MARSHALED_TYPE, GraphQLScalarType.class.getName())
                .put(NAME, getName())
                .putIfPresent(DESCRIPTION, getDescription());
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
        if (scalarType == null) {
            return scalarTypeJson.getString(NAME);
        }
        return scalarType.getName();
    }

    @Override
    public String getDescription() {
        if (scalarType == null) {
            return scalarTypeJson.getString(DESCRIPTION);
        }
        return scalarType.getDescription();
    }

    @Override
    public Coercing getCoercing() {
        if (scalarType == null) {
            return new Coercing() {
                @Override
                public Object serialize(Object input) {
                    return input;
                }

                @Override
                public Object parseValue(Object input) {
                    return input;
                }

                @Override
                public Object parseLiteral(Object input) {
                    return input;
                }
            };
        }
        return scalarType.getCoercing();
    }

    @Override
    public String toString() {
        if (scalarType == null) {
            return jsonReference.toString();
        }
        return scalarType.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraphQLScalarType original() {
        return scalarType;
    }
}
