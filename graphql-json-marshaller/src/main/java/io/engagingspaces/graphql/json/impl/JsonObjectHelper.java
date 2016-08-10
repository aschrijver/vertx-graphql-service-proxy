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

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.time.Instant;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Helper class that contains conditional setters for {@link JsonObject} creation.
 *
 * @author <a href="https://github.com/aschrijver/">Arnold Schrijver</a>
 */
public class JsonObjectHelper extends JsonObject {

    /**
     * Creates a new json object helper.
     *
     * @return the json object
     */
    public static JsonObjectHelper jsonObject() {
        return new JsonObjectHelper();
    }

    /**
     * Puts the value, if it is not {@code null}.
     *
     * @param key   the key
     * @param value the value
     * @param <T>   type parameter indicating the value type
     * @return the json object helper for fluent coding
     */
    public <T> JsonObjectHelper putIfPresent(String key, T value) {
        if (value != null) {
            put(key, value);
        }
        return this;
    }

    /**
     * Puts the list, if it is not {@code null} and not empty.
     *
     * @param key  the key
     * @param list the list
     * @param <T>  type parameter indicating the value type
     * @return the json object helper for fluent coding
     */
    public <T> JsonObjectHelper putIfPresent(String key, List<T> list) {
        if (list != null && !list.isEmpty()) {
            put(key, list);
        }
        return this;
    }

    /**
     * Puts the value in the json object at the specified key using the specified function,
     * if the predicate evaluates to {@code true}.
     *
     * @param key       the key
     * @param value     the value
     * @param predicate the predicate that determines inclusion
     * @param function  the function that creates the value to put
     * @param <T>       type parameter indicating the incoming value
     * @param <U>       type parameter indicating the value to put
     * @return the json object helper for fluent coding
     */
    public <T, U> JsonObjectHelper putIf(String key, T value, Predicate<T> predicate, Function<T, U> function) {
        if (predicate.test(value)) {
            put(key, function.apply(value));
        }
        return this;
    }

    /**
     * Puts the list at the specified key if the predicate evaluates to {@code true}.
     *
     * @param key       the key
     * @param list      the value
     * @param predicate the predicate that determines inclusion
     * @return the json object helper for fluent coding
     */
    public JsonObjectHelper putIf(String key, List<JsonObject> list, Predicate<List> predicate) {
        if (list != null && predicate.test(list)) {
            put(key, list);
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonObjectHelper put(String key, Enum value) {
        super.put(key, value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonObjectHelper put(String key, CharSequence value) {
        super.put(key, value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonObjectHelper put(String key, String value) {
        super.put(key, value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonObjectHelper put(String key, Integer value) {
        super.put(key, value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonObjectHelper put(String key, Long value) {
        super.put(key, value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonObjectHelper put(String key, Double value) {
        super.put(key, value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonObjectHelper put(String key, Float value) {
        super.put(key, value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonObjectHelper put(String key, Boolean value) {
        super.put(key, value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonObjectHelper putNull(String key) {
        super.putNull(key);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonObjectHelper put(String key, JsonObject value) {
        super.put(key, value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonObjectHelper put(String key, JsonArray value) {
        super.put(key, value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonObjectHelper put(String key, byte[] value) {
        super.put(key, value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonObjectHelper put(String key, Instant value) {
        super.put(key, value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonObjectHelper put(String key, Object value) {
        super.put(key, value);
        return this;
    }
}
