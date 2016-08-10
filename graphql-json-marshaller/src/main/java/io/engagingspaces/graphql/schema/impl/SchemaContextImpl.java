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

package io.engagingspaces.graphql.schema.impl;

import graphql.schema.*;
import io.engagingspaces.graphql.SchemaMarshallerOptions;
import io.engagingspaces.graphql.json.JsonReference;
import io.engagingspaces.graphql.json.JsonSerializable;
import io.engagingspaces.graphql.json.impl.JsonReferenceHelper;
import io.engagingspaces.graphql.schema.Marshaller;
import io.engagingspaces.graphql.schema.SchemaContext;
import io.engagingspaces.graphql.schema.SchemaDecorator;
import io.engagingspaces.graphql.schema.Unmarshaller;
import io.engagingspaces.graphql.schema.decorators.DataFetcherDO;
import io.engagingspaces.graphql.schema.decorators.GraphQLScalarTypeDO;
import io.engagingspaces.graphql.schema.decorators.TypeResolverDO;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.*;
import java.util.stream.Collectors;

import static io.engagingspaces.graphql.json.JsonReference.REF_KEY;
import static io.engagingspaces.graphql.json.impl.JsonReferenceHelper.ROOT_REFERENCE;
import static io.engagingspaces.graphql.json.impl.JsonReferenceHelper.SLASH;

/**
 * Implementation class of {@link SchemaContext}.
 *
 * @author <a href="https://github.com/aschrijver/">Arnold Schrijver</a>
 */
public class SchemaContextImpl implements SchemaContext {

    private final Map<Object, SchemaDecorator> decoratedTypes;
    private final Map<String, TypeResolver> typeResolvers;
    private final Map<String, DataFetcher> dataFetchers;
    private final Map<String, GraphQLScalarType> scalarTypes;
    private final SchemaMarshallerOptions options;
    private final JsonObject rootJson;

    /**
     * Constructor called from {@link SchemaContext}.
     *
     * @param options  the marshaller options
     * @param rootJson the root json object
     */
    public SchemaContextImpl(SchemaMarshallerOptions options, JsonObject rootJson) {
        this.decoratedTypes = new HashMap<>();
        this.typeResolvers = new HashMap<>();
        this.dataFetchers = new HashMap<>();
        this.scalarTypes = new HashMap<>();
        this.options = options;
        this.rootJson = rootJson;
    }

    /*
     * SchemaContext interface implementation
     */

    /**
     * {@inheritDoc}
     */
    @Override
    public SchemaMarshallerOptions options() {
        return options;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonReference registerTypeResolver(TypeResolverDO resolver) {
        Optional<TypeResolver> registeredResolver = typeResolvers.values().stream()
                .filter(typeResolver -> typeResolver.equals(resolver))
                .findAny();
        if (registeredResolver.isPresent()) {
            return ((TypeResolverDO) registeredResolver.get()).jsonReference();
        }
        typeResolvers.put(resolver.getId(), resolver);
        return registerDecorator(resolver);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonReference registerDataFetcher(DataFetcherDO dataFetcher) {
        Optional<DataFetcher> registeredDataFetcher = dataFetchers.values().stream()
                .filter(fetcher -> fetcher.equals(dataFetcher))
                .findAny();
        if (registeredDataFetcher.isPresent()) {
            return ((DataFetcherDO) registeredDataFetcher.get()).jsonReference();
        }
        dataFetchers.put(dataFetcher.getId(), dataFetcher);
        return registerDecorator(dataFetcher);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonReference registerScalarType(GraphQLScalarTypeDO scalarType) {
        scalarTypes.put(scalarType.getName(), scalarType);
        return registerDecorator(scalarType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonReference registerDecorator(SchemaDecorator decorator) {
        decoratedTypes.put(decorator.original() == null ? decorator.toJson() : decorator.original(), decorator);
        return JsonReferenceHelper.createReference(decorator);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<Object, SchemaDecorator> getDecoratedTypes() {
        return decoratedTypes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, TypeResolver> getTypeResolvers() {
        return typeResolvers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, DataFetcher> getDataFetchers() {
        return dataFetchers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, GraphQLScalarType> getScalarTypes() {
        return scalarTypes;
    }

    /*
     * Marshaller interface implementation
     */

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T decoratorOf(T schemaObject) {
        return decoratorOf(schemaObject, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T, U extends SchemaDecorator> T decoratorOf(T schemaObject, U parent) {
        if (schemaObject instanceof SchemaDecorator) {
            return schemaObject;
        }
        if (decoratedTypes.containsKey(schemaObject)) {
            return (T) decoratedTypes.get(schemaObject);
        }
        T decoratedType = Marshaller.createDecorator(schemaObject, this, parent);
        decoratedTypes.put(schemaObject, (SchemaDecorator) decoratedType);
        return decoratedType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> JsonObject referenceTo(T schemaObject) {
        SchemaDecorator decorator = (SchemaDecorator) schemaObject;
        boolean inlineType = (schemaObject instanceof GraphQLList || schemaObject instanceof GraphQLNonNull) &&
                ((GraphQLType) schemaObject).getName() == null;
        if (inlineType) {
            return marshall(schemaObject);
        } else if (decoratedTypes.containsValue(decorator)) {
            return decoratedTypes.get(decorator.original()).jsonReference().toJson();
        }
        throw new IllegalArgumentException("Failed to get Json reference. Unknown schema object: " + schemaObject);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> JsonObject marshall(T schemaObject) {
        if (decoratedTypes.containsKey(schemaObject)) {
            return decoratedTypes.get(schemaObject).toJson();
        } else if (schemaObject instanceof JsonSerializable) {
            return ((JsonSerializable) schemaObject).toJson();
        }
        String className = schemaObject.getClass().getName();
        throw new IllegalArgumentException(
                "Failed to marshal. Class '" + className + "' is not a known GraphQL schema class");
    }

    /*
     * Marshaller interface implementation
     */

    @Override
    @SuppressWarnings("unchecked")
    public <T extends SchemaDecorator, U extends SchemaDecorator> T dereference(Object jsonData, U parent) {
        if (decoratedTypes.containsKey(jsonData)) {
            return (T) decoratedTypes.get(jsonData);
        } else {
            JsonObject json = (JsonObject) jsonData;
            String reference = json.getString(REF_KEY);
            if (reference != null) {
                String[] referencePath = json.getString(REF_KEY).substring(ROOT_REFERENCE.length()).split(SLASH);
                json = rootJson;
                for (String ref : referencePath) {
                    json = json.getJsonObject(ref);
                }
                return unmarshall(json, parent);
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends SchemaDecorator> T unmarshall(JsonObject json) {
        return unmarshall(json, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends SchemaDecorator, U extends SchemaDecorator> T unmarshall(JsonObject json, U parent) {
        if (json == null) {
            return null;
        }
        T decorator = dereference(json, parent);
        return decorator == null ? Unmarshaller.unmarshall(json, this, parent) : decorator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> List<T> unmarshallList(JsonObject json, String listKey) {
        return unmarshallList(json, listKey, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T, U extends SchemaDecorator> List<T> unmarshallList(JsonObject json, String listKey, U parent) {
        if (json == null || !json.containsKey(listKey) || json.getValue(listKey) == null) {
            return Collections.emptyList();
        }
        Object list = json.getValue(listKey);
        if (list instanceof JsonObject) {
            return ((JsonObject) list).stream()
                    .map(entry -> (T) unmarshall((JsonObject) entry.getValue(), parent))
                    .collect(Collectors.toList());
        } else if (list instanceof JsonArray) {
            return ((JsonArray) list).stream()
                    .map(value -> (T) unmarshall((JsonObject) value, parent))
                    .collect(Collectors.toList());
        } else {
            throw new IllegalStateException("Failed to unmarshall type to list. Type: " + list.getClass().getName());
        }
    }
}
