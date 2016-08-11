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
import graphql.schema.GraphQLNonNull;
import graphql.schema.GraphQLType;
import io.engagingspaces.graphql.marshaller.json.JsonReference;
import io.engagingspaces.graphql.marshaller.json.PropNames;
import io.engagingspaces.graphql.marshaller.schema.SchemaChildDecorator;
import io.engagingspaces.graphql.marshaller.schema.SchemaContext;
import io.engagingspaces.graphql.marshaller.schema.SchemaDecorator;
import io.vertx.core.json.JsonObject;

import java.util.Collections;

import static io.engagingspaces.graphql.marshaller.schema.SchemaContext.EMPTY;

/**
 * Data object wrapper for {@link GraphQLNonNull}.
 *
 * @author <a href="https://github.com/aschrijver/">Arnold Schrijver</a>
 */
public class GraphQLNonNullDO extends GraphQLNonNull
        implements SchemaChildDecorator<GraphQLNonNull, SchemaDecorator> {

    private final GraphQLNonNull typeNonNull;
    private final JsonObject typeNonNullJson;
    private final SchemaDecorator parent;
    private final SchemaContext context;
    private final JsonReference jsonReference;

    private GraphQLNonNullDO(GraphQLNonNull typeNonNull, JsonObject typeNonNullJson,
                             SchemaDecorator parent, SchemaContext context) {
        super(new GraphQLEnumType(EMPTY, null, Collections.emptyList()));
        this.typeNonNull = typeNonNull;
        this.typeNonNullJson = typeNonNullJson;
        this.parent = parent;
        this.context = context;
        this.jsonReference = context.registerDecorator(this);
    }

    protected GraphQLNonNullDO(GraphQLNonNull typeNonNull, SchemaContext context, SchemaDecorator parent) {
        this(typeNonNull, null, parent, context);
    }

    /**
     * Creates the GraphQL object from its json serialization data using the provided schema context.
     *
     * @param json    the json data
     * @param context the schema context
     * @param parent  the parent field definition
     */
    @SuppressWarnings("unused")
    public GraphQLNonNullDO(JsonObject json, SchemaContext context, SchemaDecorator parent) {
        this(null, json, parent, context);
    }

    /**
     * Creates a decorator of the provided GraphQL object that turns it into a Vert.x data object.
     *
     * @param original the schema object to decorate
     * @param context  the schema context
     * @param parent   the parent field definition
     * @return the schema object decorated as a vertx data object
     */
    public static GraphQLNonNull create(GraphQLNonNull original, SchemaContext context, SchemaDecorator parent) {
        return new GraphQLNonNullDO(original, context, parent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonObject toJson() {
        if (typeNonNullJson != null) {
            return typeNonNullJson;
        }
        return new JsonObject()
                .put(PropNames.MARSHALED_TYPE, GraphQLNonNull.class.getName())
                .put(PropNames.WRAPPED_TYPE, context.referenceTo(getWrappedType()));
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
    public GraphQLType getWrappedType() {
        if (typeNonNull == null) {
            return context.unmarshall(typeNonNullJson.getJsonObject(PropNames.WRAPPED_TYPE));
        }
        return context.decoratorOf(typeNonNull.getWrappedType());
    }

    @Override
    public String getName() {
        if (typeNonNull == null) {
            return typeNonNullJson.getString(PropNames.NAME);
        }
        return typeNonNull.getName();
    }

    @Override
    public boolean equals(Object o) {
        if (typeNonNull == null) {
            return o instanceof GraphQLNonNullDO &&
                    ((GraphQLNonNullDO) o).jsonReference().getReference().equals(jsonReference.getReference());
        }
        return typeNonNull.equals(o);
    }

    @Override
    public int hashCode() {
        if (typeNonNull == null) {
            return jsonReference.getReference().hashCode();
        }
        return typeNonNull.hashCode();
    }

    @Override
    public String toString() {
        if (typeNonNull == null) {
            return jsonReference.toString();
        }
        return typeNonNull.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraphQLNonNull original() {
        return typeNonNull;
    }
}
