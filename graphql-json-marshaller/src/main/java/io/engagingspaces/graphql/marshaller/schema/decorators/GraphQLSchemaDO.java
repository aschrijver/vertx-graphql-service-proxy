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
import io.engagingspaces.graphql.marshaller.schema.Marshaller;
import io.engagingspaces.graphql.marshaller.schema.SchemaContext;
import io.engagingspaces.graphql.marshaller.schema.SchemaDecorator;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static io.engagingspaces.graphql.marshaller.json.PropNames.*;
import static io.engagingspaces.graphql.marshaller.schema.SchemaContext.EMPTY;

/**
 * Data object wrapper for {@link GraphQLSchema}.
 *
 * @author <a href="https://github.com/aschrijver/">Arnold Schrijver</a>
 */
public class GraphQLSchemaDO extends GraphQLSchema implements SchemaDecorator<GraphQLSchema> {

    public static final List<String> introspectionTypes = Collections.unmodifiableList(Arrays.asList(
            "__Schema",
            "__Type",
            "__TypeKind",
            "__Field",
            "__InputValue",
            "__EnumValue",
            "__Directive"
    ));

    private final GraphQLSchema schema;
    private final JsonObject rootJson;
    private final JsonObject schemaJson;
    private final SchemaContext context;
    private final JsonReference jsonReference;

    private final Predicate<GraphQLType> shouldIncludeIntrospectionTypes;
    private final Predicate<Object> shouldIncludeDirectives;

    private GraphQLSchemaDO(GraphQLSchema schema, JsonObject json, SchemaContext context) {
        super(new GraphQLObjectType(EMPTY, null, Collections.emptyList(), Collections.emptyList()));
        this.schema = schema;
        this.rootJson = json;
        this.schemaJson = json == null ? null : getSchemaJson(json);
        this.context = context;
        this.shouldIncludeDirectives = directive -> context.options().includeDirectives();
        this.shouldIncludeIntrospectionTypes = type ->
                context.options().includeIntrospectionTypes() || !introspectionTypes.contains(type.getName());

        unmarshallSchemaObjects(rootJson);

        this.jsonReference = context.registerDecorator(this);
    }

    protected GraphQLSchemaDO(GraphQLSchema schema, SchemaContext context) {
        this(schema, null, context);
    }

    /**
     * Creates the GraphQL object from its json serialization data using the provided schema context.
     *
     * @param json    the json data
     * @param context the schema context, created if not provided
     */
    @SuppressWarnings("unused")
    public GraphQLSchemaDO(JsonObject json, SchemaContext context) {
        this(null, json, context);
    }

    /**
     * Creates a decorated version of the GraphQL schema that is passed in.
     *
     * @param schema the original schema
     * @return the decorated schema
     */
    public static GraphQLSchema of(GraphQLSchema schema) {
        return new GraphQLSchemaDO(schema, Marshaller.createContext());
    }

    /**
     * Creates a decorated version of the GraphQL schema that is passed in, using the provided context.
     *
     * @param schema  the original schema
     * @param context the schema context
     * @return the decorated schema
     */
    public static GraphQLSchema of(GraphQLSchema schema, SchemaContext context) {
        return new GraphQLSchemaDO(schema, Marshaller.createContextIfMissing(context));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonObject toJson() {
        return toJson(false);
    }

    public JsonObject toJson(boolean shallow) {
        if (shallow) {
            return marshallShallow();
        }
        if (schema == null) {
            return schemaJson;
        }
        JsonObject schemaJson = new JsonObject();
        marshallTypes(schemaJson);
        marshallTypeResolvers(schemaJson);
        marshallDataFetchers(schemaJson);
        marshallScalarTypes(schemaJson);
        return marshallSchemaEntry(schemaJson);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonReference jsonReference() {
        return jsonReference;
    }

    @Override
    public Set<GraphQLType> getDictionary() {
        if (schema == null) {
            return schemaJson.getJsonArray(DICTIONARY, new JsonArray()).stream()
                    .map(type -> (GraphQLType) context.unmarshall((JsonObject) type))
                    .collect(Collectors.toSet());
        }
        return schema.getDictionary().stream()
                .map(context::decoratorOf)
                .collect(Collectors.toSet());
    }

    @Override
    public GraphQLType getType(String typeName) {
        if (schema == null) {
            return context.unmarshall(rootJson.getJsonObject(SCHEMA_TYPES, new JsonObject()).getJsonObject(typeName));
        }
        return context.decoratorOf(schema.getType(typeName));
    }

    @Override
    public List<GraphQLType> getAllTypesAsList() {
        if (schema == null) {
            return context.unmarshallList(rootJson, SCHEMA_TYPES);
        }
        return schema.getAllTypesAsList().stream()
                .map(context::decoratorOf)
                .collect(Collectors.toList());
    }

    @Override
    public GraphQLObjectType getQueryType() {
        if (context == null) {
            // called during dummy super() constructor initialization.
            return new GraphQLObjectType(EMPTY, null, Collections.emptyList(), Collections.emptyList());
        }
        return schema == null ? context.unmarshall(schemaJson.getJsonObject(QUERY_TYPE)) :
                context.decoratorOf(schema.getQueryType());
    }

    @Override
    public GraphQLObjectType getMutationType() {
        return isSupportingMutations() ? (schema == null ?
                context.unmarshall(schemaJson.getJsonObject(MUTATION_TYPE)) :
                context.decoratorOf(schema.getMutationType())) : null;
    }

    @Override
    public List<GraphQLDirective> getDirectives() {
        if (schema == null) {
            return context.unmarshallList(schemaJson, DIRECTIVES, this);
        }
        return schema.getDirectives().stream()
                .map(directive -> context.decoratorOf(directive, this))
                .collect(Collectors.toList());
    }

    @Override
    public GraphQLDirective getDirective(String name) {
        if (schema == null) {
            return context.unmarshall(schemaJson.getJsonObject(DIRECTIVES, new JsonObject()).getJsonObject(name), this);
        }
        return context.decoratorOf(schema.getDirective(name));
    }

    @Override
    public boolean isSupportingMutations() {
        if (context == null) {
            return false;
        }
        if (schema == null) {
            return schemaJson.containsKey(MUTATION_TYPE) && schemaJson.getValue(MUTATION_TYPE) instanceof JsonObject;
        }
        return schema.isSupportingMutations();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraphQLSchema original() {
        return schema;
    }

    private JsonObject marshallShallow() {
        if (schema == null) {
            return schemaJson;
        }
        return JsonObjectHelper.jsonObject()
                .put(MARSHALED_TYPE, GraphQLSchema.class.getName())
                .put(QUERY_TYPE, context.referenceTo(getQueryType()))
                .putIfPresent(MUTATION_TYPE, isSupportingMutations() ? context.referenceTo(getMutationType()) : null)
                .putIf(DIRECTIVES, getDirectives().stream()
                        .map(context::marshall)
                        .collect(Collectors.toList()), test -> context.options().includeDirectives())
                .putIfPresent(DICTIONARY, getDictionary().stream()
                        .map(context::marshall)
                        .collect(Collectors.toList()));
    }

    private void marshallTypes(JsonObject schemaJson) {
        JsonObject types = new JsonObject();
        JsonObject interfaces = new JsonObject();

        getAllTypesAsList().stream()
                .filter(shouldIncludeIntrospectionTypes)
                .filter(type -> !(type instanceof GraphQLScalarType))
                .map(context::marshall)
                .forEach(type -> {
                    if (GraphQLInterfaceType.class.getName().equals(type.getString(MARSHALED_TYPE))) {
                        interfaces.put(type.getString(NAME), type);
                    } else {
                        types.put(type.getString(NAME), type);
                    }
                });
        schemaJson.put(SCHEMA_TYPES, types);
        schemaJson.put(SCHEMA_INTERFACES, interfaces);
    }

    private void marshallTypeResolvers(JsonObject schemaJson) {
        if (!context.getTypeResolvers().isEmpty()) {
            JsonObject resolvers = new JsonObject();
            context.getTypeResolvers().entrySet()
                    .forEach(entry -> resolvers.put(entry.getKey(), context.marshall(entry.getValue())));
            schemaJson.put(TYPE_RESOLVERS, resolvers);
        }
    }

    private void marshallDataFetchers(JsonObject schemaJson) {
        if (!context.getDataFetchers().isEmpty()) {
            JsonObject fetchers = new JsonObject();
            context.getDataFetchers().entrySet()
                    .forEach(entry -> fetchers.put(entry.getKey(), context.marshall(entry.getValue())));
            schemaJson.put(DATA_FETCHERS, fetchers);
        }

    }

    private void marshallScalarTypes(JsonObject schemaJson) {
        if (!context.getScalarTypes().isEmpty()) {
            JsonObject scalars = new JsonObjectHelper();
            context.getScalarTypes().entrySet()
                    .forEach(entry -> scalars.put(entry.getKey(), context.marshall(entry.getValue())));
            schemaJson.put(SCALAR_TYPES, scalars);
        }
    }

    private JsonObject marshallSchemaEntry(JsonObject schemaJson) {
        return schemaJson.put(SCHEMAS, new JsonObject().put(jsonReference.getTargetKey(), toJson(true)));
    }

    private JsonObject getSchemaJson(JsonObject rootJson) {
        Set<String> schemas = rootJson.getJsonObject(SCHEMAS, new JsonObject()).fieldNames();
        if (schemas.size() == 0 || schemas.size() > 1) {
            throw new IllegalArgumentException("Failed to unmarshall. Expected 1 schema, found: " + schemas.size());
        }
        String schemaName = schemas.iterator().next();
        return rootJson.getJsonObject(SCHEMAS).getJsonObject(schemaName);
    }

    private void unmarshallSchemaObjects(JsonObject json) {
        if (json != null) {
            context.unmarshallList(json, SCHEMA_TYPES);
            context.unmarshallList(json, SCHEMA_INTERFACES);
            context.unmarshallList(json, SCALAR_TYPES);
            schemaJson.getJsonObject(DIRECTIVES, new JsonObject()).stream()
                    .filter(shouldIncludeDirectives)
                    .forEach(entry -> context.unmarshall((JsonObject) entry.getValue(), this));
        }
    }
}
