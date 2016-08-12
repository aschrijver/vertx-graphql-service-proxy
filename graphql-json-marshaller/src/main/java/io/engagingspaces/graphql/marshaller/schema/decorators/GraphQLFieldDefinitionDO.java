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
import io.engagingspaces.graphql.marshaller.schema.SchemaChildDecorator;
import io.engagingspaces.graphql.marshaller.schema.SchemaContext;
import io.vertx.core.json.JsonObject;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static io.engagingspaces.graphql.marshaller.json.PropNames.*;
import static io.engagingspaces.graphql.marshaller.schema.SchemaContext.EMPTY;

/**
 * Data object wrapper for {@link GraphQLFieldDefinition}.
 *
 * @author <a href="https://github.com/aschrijver/">Arnold Schrijver</a>
 */
public class GraphQLFieldDefinitionDO extends GraphQLFieldDefinition
        implements SchemaChildDecorator<GraphQLFieldDefinition, GraphQLType> {

    private final GraphQLFieldDefinition definition;
    private final JsonObject definitionJson;
    private final GraphQLType parent;
    private final SchemaContext context;
    private final JsonReference jsonReference;
    private List<GraphQLArgument> arguments;

    private GraphQLFieldDefinitionDO(GraphQLFieldDefinition definition, JsonObject definitionJson,
                                    GraphQLType parent, SchemaContext context) {
        super(EMPTY, null, new GraphQLEnumType(EMPTY, null, Collections.emptyList()),
                (environment -> null), Collections.emptyList(), null);
        this.definition = definition;
        this.definitionJson = definitionJson;
        this.parent = parent;
        this.context = context;
        this.jsonReference = context.registerDecorator(this);
    }

    protected GraphQLFieldDefinitionDO(GraphQLFieldDefinition definition, SchemaContext context, GraphQLType parent) {
        this(definition, null, parent, context);
    }

    /**
     * Creates the GraphQL object from its json serialization data using the provided schema context.
     *
     * @param json    the json data
     * @param context the schema context
     */
    @SuppressWarnings("unused")
    public GraphQLFieldDefinitionDO(JsonObject json, SchemaContext context, GraphQLType parent) {
        this(null, json, parent, context);
        context.unmarshall(json.getJsonObject(DATA_FETCHER), this);
        arguments = context.unmarshallList(json, ARGUMENTS, this);
    }

    /**
     * Creates a decorator of the provided GraphQL object that turns it into a Vert.x data object.
     *
     * @param original the schema object to decorate
     * @param context  the schema context
     * @return the schema object decorated as a vertx data object
     */
    public static GraphQLFieldDefinition create(
            GraphQLFieldDefinition original, SchemaContext context, GraphQLType parent) {
        return new GraphQLFieldDefinitionDO(original, context, parent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonObject toJson() {
        if (definitionJson != null) {
            return definitionJson;
        }
        return JsonObjectHelper.jsonObject()
                .put(MARSHALED_TYPE, GraphQLFieldDefinition.class.getName())
                .put(NAME, getName())
                .putIfPresent(DESCRIPTION, getDescription())
                .put(TYPE, context.referenceTo(getType()))
                .putIf(DATA_FETCHER, definition.getDataFetcher(),
                        fetcher -> !(fetcher instanceof PropertyDataFetcher) && !(fetcher instanceof FieldDataFetcher),
                        dataFetcher -> context.referenceTo(getDataFetcher()))
                .putIfPresent(ARGUMENTS, getArguments().stream()
                        .map(context::marshall)
                        .collect(Collectors.toList()))
                .putIfPresent(DEPRECATION_REASON, getDeprecationReason());
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

    @Override
    public String getName() {
        if (definition == null) {
            return definitionJson.getString(NAME);
        }
        return definition.getName();
    }

    @Override
    public String getDescription() {
        if (definition == null) {
            return definitionJson.getString(DESCRIPTION);
        }
        return definition.getDescription();
    }

    @Override
    public GraphQLOutputType getType() {
        if (definition == null) {
            return context.unmarshall(definitionJson.getJsonObject(TYPE));
        } else if (definition.getType() instanceof GraphQLList || definition.getType() instanceof GraphQLNonNull) {
            return context.decoratorOf(definition.getType(), this);
        }
        return context.decoratorOf(definition.getType());
    }

    @Override
    public DataFetcher getDataFetcher() {
        if (definition == null) {
            return context.unmarshall(definitionJson.getJsonObject(DATA_FETCHER), this);
        }
        return context.decoratorOf(definition.getDataFetcher(), this);
    }

    @Override
    public GraphQLArgument getArgument(String name) {
        if (definitionJson != null) {
            return  arguments.stream().filter(argument -> name.equals(argument.getName())).findAny().get();
        }
        return context.decoratorOf(definition.getArgument(name), this);
    }

    @Override
    public List<GraphQLArgument> getArguments() {
        if (definition == null) {
            return arguments;
        }
        return definition.getArguments().stream()
                .map(argument -> context.decoratorOf(argument, this))
                .collect(Collectors.toList());
    }

    @Override
    public String getDeprecationReason() {
        if (definition == null) {
            return definitionJson.getString(DEPRECATION_REASON);
        }
        return definition.getDeprecationReason();
    }

    @Override
    public boolean isDeprecated() {
        if (definition == null) {
            String reason = getDeprecationReason();
            return reason != null && !reason.isEmpty();
        }
        return definition.isDeprecated();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraphQLFieldDefinition original() {
        return definition;
    }
}
