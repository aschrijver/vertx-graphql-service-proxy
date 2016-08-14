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

import graphql.schema.GraphQLSchema;
import io.vertx.codegen.annotations.VertxGen;

/**
 * Data object wrapper for {@link GraphQLSchema}.
 *
 * @author <a href="https://github.com/aschrijver/">Arnold Schrijver</a>
 */
@VertxGen
public enum SchemaProxyType {

    /**
     * Proxies the GraphQL schema consumer-side, and delegates calls to type resolvers and data fetchers to the service.
     */
    ProxyClient,

    /**
     * Proxies the GraphQl schema as a regular Vert.x service proxy, and executes queries in the service implementation.
     */
    ServiceProxy
}
