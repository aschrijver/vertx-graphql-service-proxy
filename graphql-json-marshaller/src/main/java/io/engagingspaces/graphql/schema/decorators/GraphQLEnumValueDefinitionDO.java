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

import graphql.schema.GraphQLEnumType;
import graphql.schema.GraphQLEnumValueDefinition;
import io.engagingspaces.graphql.json.JsonReference;
import io.engagingspaces.graphql.json.impl.JsonObjectHelper;
import io.engagingspaces.graphql.schema.SchemaChildDecorator;
import io.engagingspaces.graphql.schema.SchemaContext;
import io.vertx.core.json.JsonObject;

import static io.engagingspaces.graphql.json.PropNames.*;
import static io.engagingspaces.graphql.schema.SchemaContext.EMPTY;

/**
 * Data object wrapper for {@link GraphQLEnumValueDefinition}.
 *
 * @author <a href="https://github.com/aschrijver/">Arnold Schrijver</a>
 */
public class GraphQLEnumValueDefinitionDO extends GraphQLEnumValueDefinition
        implements SchemaChildDecorator<GraphQLEnumValueDefinition, GraphQLEnumType> {

    private final GraphQLEnumValueDefinition definition;
    private final JsonObject definitionJson;
    private final GraphQLEnumType parent;
    private final SchemaContext context;
    private final JsonReference jsonReference;

    private GraphQLEnumValueDefinitionDO(GraphQLEnumValueDefinition definition, JsonObject definitionJson,
                                        GraphQLEnumType parent, SchemaContext context) {
        super(EMPTY, null, null);
        this.definition = definition;
        this.definitionJson = definitionJson;
        this.parent = parent;
        this.context = context;
        this.jsonReference = this.context.registerDecorator(this);
    }

    protected GraphQLEnumValueDefinitionDO(
            GraphQLEnumValueDefinition definition, SchemaContext context, GraphQLEnumType parent) {
        this(definition, null, parent, context);
    }

    /**
     * Creates the GraphQL object from its json serialization data using the provided schema context.
     *
     * @param json    the json data
     * @param context the schema context
     * @param parent  the parent enumeration type
     */
    @SuppressWarnings("unused")
    public GraphQLEnumValueDefinitionDO(JsonObject json, SchemaContext context, GraphQLEnumTypeDO parent) {
        this(null, json, parent, context);
    }

    /**
     * Creates a decorator of the provided GraphQL object that turns it into a Vert.x data object.
     *
     * @param original the schema object to decorate
     * @param context  the schema context
     * @param parent  the parent enumeration type
     * @return the schema object decorated as a vertx data object
     */
    public static GraphQLEnumValueDefinition create(GraphQLEnumValueDefinition original,
                                                    SchemaContext context, GraphQLEnumType parent) {
        return new GraphQLEnumValueDefinitionDO(original, context, parent);
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
                .put(MARSHALED_TYPE, GraphQLEnumValueDefinition.class.getName())
                .put(NAME, getName())
                .putIfPresent(DESCRIPTION, getDescription())
                .put(VALUE, getValue())
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
    public GraphQLEnumType getParent() {
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
    public Object getValue() {
        if (definition == null) {
            return definitionJson.getValue(VALUE);
        }
        if (definition.getValue().getClass().isEnum()) {
            return definition.getValue().toString();
        }
        return definition.getValue();
    }

    @Override
    public boolean isDeprecated() {
        if (definition == null) {
            String reason = getDeprecationReason();
            return reason != null && !reason.isEmpty();
        }
        return definition.isDeprecated();
    }

    @Override
    public String getDeprecationReason() {
        if (definition == null) {
            return definitionJson.getString(DEPRECATION_REASON);
        }
        return definition.getDeprecationReason();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraphQLEnumValueDefinition original() {
        return definition;
    }
}
