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

package io.engagingspaces.graphql.marshaller;

import graphql.schema.*;
import io.engagingspaces.graphql.marshaller.schema.SchemaDecorator;
import io.vertx.core.json.JsonObject;
import org.example.servicediscovery.server.droids.DroidsSchema;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Scanner;

import static org.junit.Assert.*;

/**
 * Test class for marshaling {@link GraphQLSchema} to JSON.
 *
 * @author <a href="https://github.com/aschrijver/">Arnold Schrijver</a>
 */
public class SchemaMarshallerTest {

    private static JsonObject marshaledSchema;
    private static GraphQLSchema originalSchema;

    @BeforeClass
    public static void initialize() throws URISyntaxException {
        URL resource = SchemaMarshallerTest.class.getClassLoader().getResource("droids-schema-marshaled.json");
        if (resource == null) {
            fail("Serialized test data not found");
        }
        File marshaled = new File(resource.toURI());
        StringBuilder result = new StringBuilder("");
        try (Scanner scanner = new Scanner(marshaled)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                result.append(line).append("\n");
            }

            scanner.close();
        } catch (IOException ex) {
            fail(ex.getMessage());
        }
        marshaledSchema = new JsonObject(result.toString());
        originalSchema = DroidsSchema.get().schema();
    }

    @Test
    public void should_Marshall_GraphQL_Schema_To_Json() {
        // given
        GraphQLSchema schema = originalSchema;

        // when
        GraphQLSchema decorator = SchemaMarshaller.decorateSchema(schema);
        JsonObject schemaJson = ((SchemaDecorator) decorator).toJson();

        // then
        assertNotNull(schemaJson);
        System.out.println(schemaJson.encodePrettily());
    }

    @Test
    public void should_Unmarshall_GraphQL_Schema_From_Json() {
        // given
        JsonObject schemaJson = marshaledSchema;

        // when
        GraphQLSchema schema = SchemaMarshaller.fromJson(schemaJson);

        // then
        assertNotNull(schema);
        assertEquals(3, schema.getAllTypesAsList().size());
        assertNotNull(schema.getQueryType());
        assertNull(schema.getMutationType());
        assertEquals(false, schema.isSupportingMutations());
        assertNotNull(schema.getDictionary());
        assertEquals(0, schema.getDictionary().size());
        assertNotNull(schema.getDirectives());
        assertEquals(0, schema.getDirectives().size());

        compareObjectType(originalSchema.getQueryType(), schema.getQueryType());
        assertEquals(originalSchema.getQueryType().getInterfaces().size(),
                schema.getQueryType().getInterfaces().size());
        compareInterface(((GraphQLObjectType) originalSchema.getType("Droid")).getInterfaces().get(0),
                ((GraphQLObjectType) schema.getType("Droid")).getInterfaces().get(0));
        originalSchema.getAllTypesAsList().stream()
                .filter(type -> type.getName().equals(originalSchema.getQueryType().getName()))
                .forEach(type -> {
                    if (type instanceof GraphQLObjectType) {
                        compareObjectType((GraphQLObjectType) type, (GraphQLObjectType) schema.getType(type.getName()));
                    } else if (type instanceof GraphQLEnumType) {
                        compareEnumType((GraphQLEnumType) type, (GraphQLEnumType) schema.getType(type.getName()));
                    }
                });
    }

    private void compareObjectType(GraphQLObjectType original, GraphQLObjectType decorator) {
        assertEquals(original.getName(), decorator.getName());
        assertEquals(original.getDescription(), decorator.getDescription());
        assertEquals(original.getFieldDefinitions().size(), decorator.getFieldDefinitions().size());
        assertEquals(original.getInterfaces().size(), decorator.getInterfaces().size());
        original.getFieldDefinitions().stream().forEach(field ->
                compareField(field, decorator.getFieldDefinition(field.getName())));
    }

    private void compareInterface(GraphQLInterfaceType original, GraphQLInterfaceType decorator) {
        assertEquals(original.getName(), decorator.getName());
        assertEquals(original.getDescription(), decorator.getDescription());
        assertEquals(original.getFieldDefinitions().size(), decorator.getFieldDefinitions().size());
        original.getFieldDefinitions().stream().forEach(field ->
                compareField(field, decorator.getFieldDefinition(field.getName())));
    }

    private void compareEnumType(GraphQLEnumType original, GraphQLEnumType decorator) {
        assertEquals(original.getName(), decorator.getName());
        assertEquals(original.getDescription(), decorator.getDescription());
        assertEquals(original.getCoercing(), decorator.getCoercing());
        original.getValues().forEach(value -> assertTrue(decorator.getValues().contains(value)));
    }

    private void compareField(GraphQLFieldDefinition original, GraphQLFieldDefinition decorator) {
        assertEquals(original.getName(), decorator.getName());
        assertEquals(original.getDescription(), decorator.getDescription());
        assertEquals(original.getDeprecationReason(), decorator.getDeprecationReason());
        assertEquals(original.isDeprecated(), decorator.isDeprecated());
        assertEquals(original.getArguments().size(), decorator.getArguments().size());
        original.getArguments().forEach(argument ->
                compareArgument(argument, decorator.getArgument(argument.getName())));
    }

    private void compareArgument(GraphQLArgument original, GraphQLArgument decorator) {
        assertEquals(original.getName(), decorator.getName());
        assertEquals(original.getDescription(), decorator.getDescription());
        assertEquals(original.getDefaultValue(), decorator.getDefaultValue());
    }
}
