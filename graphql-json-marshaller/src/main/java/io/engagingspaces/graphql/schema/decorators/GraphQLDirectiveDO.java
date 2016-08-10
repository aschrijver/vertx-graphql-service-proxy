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

import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLDirective;
import graphql.schema.GraphQLSchema;
import io.engagingspaces.graphql.json.JsonReference;
import io.engagingspaces.graphql.json.impl.JsonObjectHelper;
import io.engagingspaces.graphql.schema.SchemaChildDecorator;
import io.engagingspaces.graphql.schema.SchemaContext;
import io.vertx.core.json.JsonObject;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static io.engagingspaces.graphql.json.PropNames.*;
import static io.engagingspaces.graphql.schema.SchemaContext.EMPTY;

/**
 * Data object wrapper for {@link GraphQLDirective}.
 *
 * @author <a href="https://github.com/aschrijver/">Arnold Schrijver</a>
 */
public class GraphQLDirectiveDO extends GraphQLDirective
        implements SchemaChildDecorator<GraphQLDirective, GraphQLSchema> {

    private final GraphQLDirective directive;
    private final JsonObject directiveJson;
    private final GraphQLSchema parent;
    private final SchemaContext context;
    private final JsonReference jsonReference;
    private List<GraphQLArgument> arguments;

    private GraphQLDirectiveDO(GraphQLDirective directive, JsonObject directiveJson,
                              GraphQLSchema parent, SchemaContext context) {
        super(EMPTY, null, Collections.emptyList(), false, false, false);
        this.directive = directive;
        this.directiveJson = directiveJson;
        this.parent = parent;
        this.context = context;
        this.jsonReference = context.registerDecorator(this);
    }

    protected GraphQLDirectiveDO(GraphQLDirective directive, SchemaContext context, GraphQLSchema parent) {
        this(directive, null, parent, context);
    }

    /**
     * Creates the GraphQL object from its json serialization data using the provided schema context.
     *
     * @param json    the json data
     * @param context the schema context
     */
    @SuppressWarnings("unused")
    public GraphQLDirectiveDO(JsonObject json, SchemaContext context, GraphQLSchemaDO parent) {
        this(null, json, parent, context);
        arguments = context.unmarshallList(json, ARGUMENTS, this);
    }

    /**
     * Creates a decorator of the provided GraphQL object that turns it into a Vert.x data object.
     *
     * @param original the schema object to decorate
     * @param context  the schema context
     * @param parent   the parent schema
     * @return the schema object decorated as a vertx data object
     */
    public static GraphQLDirective create(GraphQLDirective original, SchemaContext context, GraphQLSchema parent) {
        return new GraphQLDirectiveDO(original, context, parent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonObject toJson() {
        if (directiveJson != null) {
            return directiveJson;
        }
        return JsonObjectHelper.jsonObject()
                .put(MARSHALED_TYPE, GraphQLDirective.class.getName())
                .put(NAME, getName())
                .putIfPresent(DESCRIPTION, getDescription())
                .putIfPresent(ARGUMENTS, getArguments().stream()
                        .map(context::marshall)
                        .collect(Collectors.toList()))
                .put(IS_ON_OPERATION, isOnOperation())
                .put(IS_ON_FRAGMENT, isOnFragment())
                .put(IS_ON_FIELD, isOnField());
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
    public GraphQLSchema getParent() {
        return parent;
    }

    @Override
    public String getName() {
        if (directive == null) {
            return directiveJson.getString(NAME);
        }
        return directive.getName();
    }

    @Override
    public List<GraphQLArgument> getArguments() {
        if (directive == null) {
            return arguments;
        }
        return directive.getArguments().stream()
                .map(argument -> context.decoratorOf(argument, this))
                .collect(Collectors.toList());
    }

    @Override
    public GraphQLArgument getArgument(String name) {
        if (directive == null) {
            return  arguments.stream().filter(argument -> name.equals(argument.getName())).findAny().get();
        }
        return context.decoratorOf(directive.getArgument(name));
    }

    @Override
    public boolean isOnOperation() {
        if (directive == null) {
            return directiveJson.getBoolean(IS_ON_OPERATION);
        }
        return directive.isOnOperation();
    }

    @Override
    public boolean isOnFragment() {
        if (directive == null) {
            return directiveJson.getBoolean(IS_ON_FRAGMENT);
        }
        return directive.isOnFragment();
    }

    @Override
    public boolean isOnField() {
        if (directive == null) {
            return directiveJson.getBoolean(IS_ON_FIELD);
        }
        return directive.isOnField();
    }

    @Override
    public String getDescription() {
        if (directive == null) {
            return directiveJson.getString(DESCRIPTION);
        }
        return directive.getDescription();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraphQLDirective original() {
        return directive;
    }
}
