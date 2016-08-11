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

import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLType;
import graphql.schema.TypeResolver;
import io.engagingspaces.graphql.marshaller.json.JsonReference;
import io.engagingspaces.graphql.marshaller.json.JsonSerializable;
import io.engagingspaces.graphql.marshaller.json.PropNames;
import io.engagingspaces.graphql.marshaller.schema.SchemaChildDecorator;
import io.engagingspaces.graphql.marshaller.schema.SchemaContext;
import io.vertx.core.json.JsonObject;

import java.util.UUID;

/**
 * Data object for {@link TypeResolver}.
 *
 * @author <a href="https://github.com/aschrijver/">Arnold Schrijver</a>
 */
public class TypeResolverDO implements TypeResolver, SchemaChildDecorator<TypeResolver, GraphQLType> {

    private final String id;
    private final TypeResolver resolver;
    private final JsonObject resolverJson;
    private final GraphQLType parent;
    private final SchemaContext context;
    private final JsonReference jsonReference;

    private TypeResolverDO(TypeResolver resolver, JsonObject resolverJson, GraphQLType parent, SchemaContext context) {
        this.resolverJson = resolverJson;
        this.parent = parent;
        this.context = context;
        this.id = resolverJson == null ? UUID.randomUUID().toString() : resolverJson.getString(PropNames.ID);
        if (resolver == null) {
            if (id != null && context.getTypeResolvers().containsKey(id)) {
                this.resolver = context.getTypeResolvers().get(id);
            } else {
                this.resolver = (object -> null);
            }
        } else {
            this.resolver = resolver;
        }
        this.jsonReference = context.registerTypeResolver(this);
    }

    protected TypeResolverDO(TypeResolver resolver, SchemaContext context, GraphQLType parent) {
        this(resolver, null, parent, context);
    }

    /**
     * Creates the GraphQL object from its json serialization data using the provided schema context.
     *
     * @param json    the json data
     * @param context the schema context
     * @param parent  the parent interface or union type
     */
    @SuppressWarnings("unused")
    public TypeResolverDO(JsonObject json, SchemaContext context, GraphQLType parent) {
        this(null, json, parent, context);
    }

    /**
     * Creates a decorator of the provided GraphQL type resolver that turns it into a Vert.x data object.
     *
     * @param resolver the type resolver
     * @param context the schema context
     * @return the type resolver decorated as a vertx data object
     */
    public static TypeResolver create(TypeResolver resolver, SchemaContext context, GraphQLType parent) {
        return new TypeResolverDO(resolver, context, parent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonObject toJson() {
        if (resolverJson != null) {
            return resolverJson;
        }
        return new JsonObject()
                .put(PropNames.MARSHALED_TYPE, TypeResolver.class.getName())
                .put(PropNames.MARSHALED_TYPE_CLASS, resolver.getClass().getName())
                .put(PropNames.PARENT, new JsonObject().put(JsonReference.REF_KEY, ((JsonSerializable) parent).jsonReference().getReference()))
                .put(PropNames.ID, getId());
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
    public GraphQLType getParent() {
        return parent;
    }

    /**
     * Gets the ID of the data fetcher that is serialized to Json.
     *
     * @return the id of the data fetcher
     */
    public String getId() {
        return id;
    }

    @Override
    public GraphQLObjectType getType(Object object) {
        return resolver.getType(object);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TypeResolver original() {
        return resolver;
    }
}
