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

package io.engagingspaces.graphql.json.impl;

import graphql.schema.*;
import io.engagingspaces.graphql.json.JsonReference;
import io.engagingspaces.graphql.schema.SchemaChildDecorator;
import io.engagingspaces.graphql.schema.SchemaDecorator;
import io.engagingspaces.graphql.schema.decorators.DataFetcherDO;
import io.engagingspaces.graphql.schema.decorators.TypeResolverDO;
import io.vertx.core.json.JsonObject;

import static io.engagingspaces.graphql.json.PropNames.*;

/**
 * Helper class for constructing {@link JsonReference} instances.
 *
 * @author <a href="https://github.com/aschrijver/">Arnold Schrijver</a>
 */
public interface JsonReferenceHelper {

    /**
     * Json reference path to the root of the schema json.
     */
    String ROOT_REFERENCE = "#/";

    /**
     * Json reference path to the collection of schema's (usually one, but can be more).
     */
    String SCHEMAS_REFERENCE = ROOT_REFERENCE + "__schemas/";

    /**
     * Json reference path to the collection of types defined in the schema.
     */
    String TYPES_REFERENCE = ROOT_REFERENCE + "__types/";

    /**
     * Json reference path to the collection of interfaces defined in the schema.
     */
    String INTERFACES_REFERENCE = ROOT_REFERENCE + "__interfaces/";

    /**
     * Json reference path to the collection of type resolvers defined in the schema.
     */
    String RESOLVERS_REFERENCE = ROOT_REFERENCE + "__typeResolvers/";

    /**
     * Json reference path to the collection of data fetchers defined in the schema.
     */
    String FETCHERS_REFERENCE = ROOT_REFERENCE + "__dataFetchers/";

    /**
     * Json reference path to the collection of scalar types defined in the schema.
     */
    String SCALARS_REFERENCE = ROOT_REFERENCE + "__scalarTypes/";

    String SLASH = "/";

    /**
     * Creates a Json reference from the provided decorator instance.
     *
     * @param decorator the decorator instance
     * @return the json reference
     */
    static JsonReference createReference(SchemaDecorator decorator) {
        String reference = null;
        if (decorator instanceof GraphQLScalarType) {
            reference = SCALARS_REFERENCE + ((GraphQLScalarType) decorator).getName();
        } else if (decorator instanceof TypeResolver) {
            reference = RESOLVERS_REFERENCE + ((TypeResolverDO) decorator).getId();
        } else if (decorator instanceof DataFetcher) {
            reference = FETCHERS_REFERENCE + ((DataFetcherDO) decorator).getId();
        } else if (decorator instanceof SchemaChildDecorator) {
            return createReference((SchemaChildDecorator) decorator);
        } else if (decorator instanceof GraphQLInterfaceType) {
            reference = INTERFACES_REFERENCE + ((GraphQLInterfaceType) decorator).getName();
        } else if (decorator instanceof GraphQLType) {
            reference = TYPES_REFERENCE + ((GraphQLType) decorator).getName();
        } else if (decorator instanceof GraphQLSchema) {
            reference = SCHEMAS_REFERENCE + ((GraphQLSchema) decorator).getQueryType().getName();
        }
        return JsonReference.create(reference, decorator);
    }

    /**
     * Creates a Json reference from the provided child decorator instance.
     *
     * @param decorator the child decorator instance
     * @return the json reference
     */
    static JsonReference createReference(SchemaChildDecorator decorator) {
        SchemaDecorator parent = (SchemaDecorator) decorator.getParent();
        String parentReference = parent.jsonReference().getReference() + SLASH;
        String reference = null;
        if (decorator instanceof GraphQLFieldDefinition) {
            parentReference += FIELD_DEFINITIONS + SLASH;
            reference = ((GraphQLFieldDefinition) decorator).getName();
        } else if (decorator instanceof GraphQLArgument) {
            parentReference += ARGUMENTS + SLASH;
            reference = ((GraphQLArgument) decorator).getName();
        } else if (decorator instanceof GraphQLList || decorator instanceof GraphQLNonNull) {
            parentReference += WRAPPED_TYPE + SLASH;
            reference = "";
        } else if (decorator instanceof GraphQLType) {
            reference = ((GraphQLType) decorator).getName();
        } else if (decorator instanceof GraphQLDirective) {
            reference = ((GraphQLDirective) decorator).getName();
        } else if (decorator instanceof GraphQLEnumValueDefinition) {
            reference = ((GraphQLEnumValueDefinition) decorator).getName();
        }
        if (reference == null) {
            throw new IllegalStateException("Unknown decorator type: " + decorator.getClass().getName());
        }
        return JsonReference.create(parentReference + reference, decorator);
    }
}
