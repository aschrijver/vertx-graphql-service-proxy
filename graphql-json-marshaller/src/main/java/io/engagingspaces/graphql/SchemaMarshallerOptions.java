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

package io.engagingspaces.graphql;

/**
 * Options for marshaling and un-marshalling {@link graphql.schema.GraphQLSchema} instances.
 *
 * @author <a href="https://github.com/aschrijver/">Arnold Schrijver</a>
 */
public class SchemaMarshallerOptions {

    private boolean includeIntrospectionTypes;
    private boolean includeDirectives;

    /**
     * Protected constructor (use {@code SchemaMarshallerOptions.create()}).
     */
    protected SchemaMarshallerOptions() {
    }

    /**
     * Creates a new schema marshaller options instance.
     *
     * @return the schema marshaller options
     */
    public static SchemaMarshallerOptions create() {
        return new SchemaMarshallerOptions();
    }

    /**
     * Option that determines whether GraphQL introspection types should be marshaled or not.
     *
     * @return {@code true} to marshall introspection types, {@code false} otherwise (default)
     */
    public boolean includeIntrospectionTypes() {
        return includeIntrospectionTypes;
    }

    /**
     * Sets the option that determines introspection types are marhaled or not.
     *
     * @param include {@code true} to include introspection types, or {@code false} to exclude them (default)
     * @return the marshaller options for fluent coding
     */
    public SchemaMarshallerOptions setIncludeIntrospectionTypes(boolean include) {
        includeIntrospectionTypes = include;
        return this;
    }

    /**
     * Option that determines whether GraphQL directives should be marshaled or not.
     *
     * @return {@code true} to marshall introspection types, {@code false} otherwise (default)
     */
    public boolean includeDirectives() {
        return  includeDirectives;
    }

    /**
     * Sets the option that determines whether directives are marshaled or not.
     *
     * @param include {@code true} to include directives, or {@code false} to exclude them (default)
     * @return the marshaller options for fluent coding
     */
    public SchemaMarshallerOptions setIncludeDirectives(boolean include) {
        includeDirectives = include;
        return this;
    }
 }
