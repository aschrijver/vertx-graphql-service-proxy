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

package io.engagingspaces.graphql.proxy.impl;

import io.engagingspaces.graphql.marshaller.SchemaMarshallerOptions;
import io.engagingspaces.graphql.marshaller.schema.Marshaller;
import io.engagingspaces.graphql.marshaller.schema.SchemaContext;
import io.engagingspaces.graphql.marshaller.schema.Unmarshaller;
import io.engagingspaces.graphql.marshaller.schema.impl.SchemaContextImpl;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * Schema context implementation for {@link io.engagingspaces.graphql.proxy.GraphQLSchemaProxy}.
 *
 * @author <a href="https://github.com/aschrijver/">Arnold Schrijver</a>
 */
public class SchemaProxyContext extends SchemaContextImpl {

    private final Vertx vertx;

    protected SchemaProxyContext(Vertx vertx, SchemaMarshallerOptions marshallerOptions) {
        super(marshallerOptions, null);
        this.vertx = vertx;
    }

    protected SchemaProxyContext(Vertx vertx, SchemaMarshallerOptions marshallerOptions, JsonObject json) {
        super(marshallerOptions, json);
        this.vertx = vertx;
    }

    public static SchemaContext createMarshalingContext(SchemaMarshallerOptions marshallerOptions) {
        return Marshaller.createContext(marshallerOptions);
    }

    public static SchemaContext createUnmarshalingContext(
            SchemaMarshallerOptions marshallerOptions, JsonObject rootJson) {
        return Unmarshaller.createContext(marshallerOptions, rootJson);
    }
}
