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

package io.engagingspaces.graphql.json;

import io.vertx.core.json.JsonObject;

/**
 * Interface for GraphQL objects that can be serialized to JSON.
 *
 * @author <a href="https://github.com/aschrijver/">Arnold Schrijver</a>
 */
public interface JsonSerializable {

    /**
     * Converts the GraphQL object to json.
     *
     * @return the json result
     */
    JsonObject toJson();

    /**
     * Returns a JSON reference to schema object serialization data, and that allows retrieval of the object itself.
     *
     * @return the json reference
     */
    JsonReference jsonReference();
}
