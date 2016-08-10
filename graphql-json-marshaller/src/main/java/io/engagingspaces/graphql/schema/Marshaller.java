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

package io.engagingspaces.graphql.schema;

import graphql.schema.*;
import io.engagingspaces.graphql.SchemaMarshallerOptions;
import io.engagingspaces.graphql.schema.decorators.*;
import io.engagingspaces.graphql.schema.impl.SchemaContextImpl;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

/**
 * Interface for marshalling GraphQL schema objects to JSON.
 *
 * @author <a href="https://github.com/aschrijver/">Arnold Schrijver</a>
 */
public interface Marshaller {

    /**
     * Creates a new schema context.
     *
     * @return the schema context
     */
    static SchemaContext createContext() {
        return new SchemaContextImpl(SchemaMarshallerOptions.create(), null);
    }

    /**
     * Creates a new schema context for marshalling using the provided marshaling options.
     *
     * @param options  the marshaller options
     * @return the schema context
     */
    static SchemaContext createContext(SchemaMarshallerOptions options) {
        return new SchemaContextImpl(options == null ? SchemaMarshallerOptions.create() : options, null);
    }

    /**
     * Creates a new schema context, if none was provided.
     *
     * @param context the schema context to use, or null
     * @return the provided context, or a newly created schema context
     */
    static SchemaContext createContextIfMissing(SchemaContext context) {
        if (context == null) {
            return createContext();
        }
        return context;
    }

    /**
     * Creates a decorated version of the schema object, using the provided schema context.
     *
     * @param obj     the original schema object to decorate
     * @param context the schema context
     * @param <U>     type parameter indicating the type of the original schema object
     * @param <T>     type parameter indicating the type of the returned decorator
     * @return the decorated schema object
     */
    static <U, T extends U> T createDecorator(U obj, SchemaContext context) {
        return createDecorator(obj, context, null);
    }

    /**
     * Creates a decorated version of the schema object, using the provided schema context and parent.
     * <p>
     * The parent object is accessible via the {@link SchemaChildDecorator#getParent()} on the returned decorator,
     * and is also used for the construction of the {@link io.engagingspaces.graphql.json.JsonReference}.
     *
     * @param obj     the original schema object to decorate
     * @param context the schema context
     * @param parent  the parent schema object decorator
     * @param <U>     type parameter indicating the type of the original schema object
     * @param <T>     type parameter indicating the type of the returned decorator
     * @param <R>     type parameter indicating the type of the parent decorator
     * @return the decorated schema object
     */
    @SuppressWarnings("unchecked")
    static <U, T extends U, R extends SchemaDecorator> T createDecorator(U obj, SchemaContext context, R parent) {
        Objects.requireNonNull(obj, "GraphQL object cannot be null");
        String className = obj.getClass().getName();
        T result;
        switch (className) {
            case "graphql.schema.GraphQLScalarType":
                result = (T) GraphQLScalarTypeDO.create((GraphQLScalarType) obj, context);
                break;
            case "graphql.schema.GraphQLNonNull":
                result = (T) GraphQLNonNullDO.create((GraphQLNonNull) obj, context, parent);
                break;
            case "graphql.schema.GraphQLArgument":
                result = (T) GraphQLArgumentDO.create((GraphQLArgument) obj, context, parent);
                break;
            case "graphql.schema.GraphQLFieldDefinition":
                result = (T) GraphQLFieldDefinitionDO.create(
                        (GraphQLFieldDefinition) obj, context, (GraphQLType) parent);
                break;
            case "graphql.schema.DataFetcher":
                result = (T) DataFetcherDO.create((DataFetcher) obj, context, (GraphQLFieldDefinition) parent);
                break;
            case "graphql.schema.GraphQLEnumType":
                result = (T) GraphQLEnumTypeDO.create((GraphQLEnumType) obj, context);
                break;
            case "graphql.schema.GraphQLEnumValueDefinition":
                result = (T) GraphQLEnumValueDefinitionDO.create(
                        (GraphQLEnumValueDefinition) obj, context, (GraphQLEnumType) parent);
                break;
            case "graphql.schema.GraphQLObjectType":
                result = (T) GraphQLObjectTypeDO.create((GraphQLObjectType) obj, context);
                break;
            case "graphql.schema.GraphQLInterfaceType":
                result = (T) GraphQLInterfaceTypeDO.create((GraphQLInterfaceType) obj, context);
                break;
            case "graphql.schema.TypeResolver":
                result = (T) TypeResolverDO.create((TypeResolver) obj, context, (GraphQLType) parent);
                break;
            case "graphql.schema.GraphQLInputObjectType":
                result = (T) GraphQLInputObjectTypeDO.create((GraphQLInputObjectType) obj, context);
                break;
            case "graphql.schema.GraphQLInputObjectField":
                result = (T) GraphQLInputObjectFieldDO.create(
                        (GraphQLInputObjectField) obj, context, (GraphQLInputObjectType) parent);
                break;
            case "graphql.schema.GraphQLList":
                result = (T) GraphQLListDO.create((GraphQLList) obj, context, parent);
                break;
            case "graphql.schema.GraphQLUnionType":
                result = (T) GraphQLUnionTypeDO.create((GraphQLUnionType) obj, context);
                break;
            case "graphql.schema.GraphQLTypeReference":
                result = (T) GraphQLTypeReferenceDO.create((GraphQLTypeReference) obj, context);
                break;
            case "graphql.schema.GraphQLDirective":
                result = (T) GraphQLDirectiveDO.create((GraphQLDirective) obj, context, (GraphQLSchema) parent);
                break;
            case "graphql.schema.GraphQLSchema":
                result = (T) GraphQLSchemaDO.of((GraphQLSchema) obj, context);
                break;
            default:
                if (obj instanceof TypeResolver) {
                    return  (T) TypeResolverDO.create((TypeResolver) obj, context, (GraphQLType) parent);
                } else if (obj instanceof DataFetcher) {
                    return (T) DataFetcherDO.create((DataFetcher) obj, context, (GraphQLFieldDefinition) parent);
                }
                throw new IllegalArgumentException("Failed to decorate GraphQL object. Class '" + className +
                        "' is not a known GraphQL schema class");
        }
        return result;
    }

    /**
     * Marshall the schema object to Json.
     *
     * @param schemaObject the schema object
     * @param <T>          type parameter indicating the type of the schema object
     * @return the serialized json object
     */
    <T> JsonObject marshall(T schemaObject);

    /**
     * Gets a decorator of the schema object.
     *
     * @param schemaObject the schema object
     * @param <T>          type parameter indicating the type of the schema object
     * @return the decorated schema object
     */
    <T> T decoratorOf(T schemaObject);

    /**
     * Gets a decorator of the schema object, with the provided parent.
     *
     * @param schemaObject the schema object
     * @param parent       the parent object
     * @param <T>          type parameter indicating the type of the returned decorate
     * @param <U>          type parameter indicating the type of the schema object
     * @return the decorated schema object
     */
    <T, U extends SchemaDecorator> T decoratorOf(T schemaObject, U parent);

    /**
     * Creates a Json object that holds a serialized json reference to the provided schema object.
     * <p>
     * If the schema object is of type {@link GraphQLList} or {@link GraphQLNonNull} then the returned json
     * is the marshaled decorator of these objects with a Json reference to the wrapped type.
     *
     * @param schemaObject the schema object
     * @param <T>          type parameter indicating the type of the schema object
     * @return the json containing the reference
     */
    <T> JsonObject referenceTo(T schemaObject);
}
