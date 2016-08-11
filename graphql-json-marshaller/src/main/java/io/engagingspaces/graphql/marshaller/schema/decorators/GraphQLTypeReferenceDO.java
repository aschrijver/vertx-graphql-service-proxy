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

import graphql.schema.GraphQLTypeReference;
import io.engagingspaces.graphql.marshaller.json.JsonReference;
import io.engagingspaces.graphql.marshaller.schema.SchemaContext;
import io.engagingspaces.graphql.marshaller.schema.SchemaDecorator;
import io.vertx.core.json.JsonObject;

import static io.engagingspaces.graphql.marshaller.json.PropNames.MARSHALED_TYPE;
import static io.engagingspaces.graphql.marshaller.json.PropNames.NAME;
import static io.engagingspaces.graphql.marshaller.schema.SchemaContext.EMPTY;

/**
 * Data object wrapper for {@link GraphQLTypeReference}.
 *
 * @author <a href="https://github.com/aschrijver/">Arnold Schrijver</a>
 */
public class GraphQLTypeReferenceDO extends GraphQLTypeReference
        implements SchemaDecorator<GraphQLTypeReference> {

    private final GraphQLTypeReference reference;
    private final JsonObject referenceJson;
    private final SchemaContext context;
    private final JsonReference jsonReference;

    private GraphQLTypeReferenceDO(GraphQLTypeReference reference, JsonObject referenceJson, SchemaContext context) {
        super(EMPTY);
        this.reference = reference;
        this.referenceJson = referenceJson;
        this.context = context;
        this.jsonReference = context.registerDecorator(this);
    }

    protected GraphQLTypeReferenceDO(GraphQLTypeReference reference, SchemaContext context) {
        this(reference, null, context);
    }

    @SuppressWarnings("unused")
    public GraphQLTypeReferenceDO(JsonObject json, SchemaContext context) {
        this(null, json, context);
    }

    /**
     * Creates a decorator of the provided GraphQL object that turns it into a Vert.x data object.
     *
     * @param original the schema object to decorate
     * @param context  the schema context
     * @return the schema object decorated as a vertx data object
     */
    public static GraphQLTypeReference create(GraphQLTypeReference original, SchemaContext context) {
        return new GraphQLTypeReferenceDO(original, context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonObject toJson() {
        if (referenceJson != null) {
            return referenceJson;
        }
        return new JsonObject()
                .put(MARSHALED_TYPE, GraphQLTypeReference.class.getName())
                .put(NAME, getName());
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
        if (reference == null) {
            return referenceJson.getString(NAME);
        }
        return reference.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraphQLTypeReference original() {
        return reference;
    }
}
