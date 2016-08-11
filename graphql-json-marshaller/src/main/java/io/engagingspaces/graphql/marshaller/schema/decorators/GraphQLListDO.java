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
import graphql.schema.GraphQLList;
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
 * Data object wrapper for {@link GraphQLList}.
 *
 * @author <a href="https://github.com/aschrijver/">Arnold Schrijver</a>
 */
public class GraphQLListDO extends GraphQLList
        implements SchemaChildDecorator<GraphQLList, SchemaDecorator> {

    private final GraphQLList list;
    private final JsonObject listJson;
    private final SchemaDecorator parent;
    private final SchemaContext context;
    private final JsonReference jsonReference;

    private GraphQLListDO(GraphQLList list, JsonObject listJson, SchemaDecorator parent, SchemaContext context) {
        super(new GraphQLEnumType(EMPTY, null, Collections.emptyList()));
        this.list = list;
        this.listJson = listJson;
        this.parent = parent;
        this.context = context;
        this.jsonReference = context.registerDecorator(this);
    }

    protected GraphQLListDO(GraphQLList list, SchemaContext context, SchemaDecorator parent) {
        this(list, null, parent, context);
    }

    /**
     * Creates the GraphQL object from its json serialization data using the provided schema context.
     *
     * @param json    the json data
     * @param context the schema context
     * @param parent  the parent field definition
     */
    @SuppressWarnings("unused")
    public GraphQLListDO(JsonObject json, SchemaContext context, SchemaDecorator parent) {
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
    public static GraphQLList create(GraphQLList original, SchemaContext context, SchemaDecorator parent) {
        return new GraphQLListDO(original, context, parent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonObject toJson() {
        if (listJson != null) {
            return listJson;
        }
        return new JsonObject()
                .put(PropNames.MARSHALED_TYPE, GraphQLList.class.getName())
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
    public String getName() {
        if (list == null) {
            return listJson.getString(PropNames.NAME);
        }
        return list.getName();
    }

    @Override
    public GraphQLType getWrappedType() {
        if (list == null) {
            return context.unmarshall(listJson.getJsonObject(PropNames.WRAPPED_TYPE));
        }
        return context.decoratorOf(list.getWrappedType());
    }

    @Override
    public boolean equals(Object o) {
        if (list == null) {
            return o instanceof GraphQLListDO &&
                    ((GraphQLListDO) o).jsonReference().getReference().equals(jsonReference.getReference());
        }
        return list.equals(o);
    }

    @Override
    public int hashCode() {
        if (list == null) {
            return jsonReference.getReference().hashCode();
        }
        return list.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraphQLList original() {
        return list;
    }
}
