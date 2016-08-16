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

package io.engagingspaces.graphql.marshaller.json;

import io.engagingspaces.graphql.marshaller.json.impl.JsonReferenceImpl;
import io.engagingspaces.graphql.marshaller.schema.SchemaDecorator;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

/**
 * Represents a JSON reference to another GraphQL schema object.
 * <p>
 * The JSON reference is formatted according to
 * IETF draft (See: https://tools.ietf.org/html/draft-pbryan-zyp-json-ref-03).
 * <p>
 * The reference points to a target object (usually a sub-type of
 * {@link SchemaDecorator}).
 *
 * @author <a href="https://github.com/aschrijver/">Arnold Schrijver</a>
 */
public interface JsonReference {

    /**
     * Json key for Json reference values.
     */
    String REF_KEY = "$ref";

    /**
     * Creates a new Json reference instance.
     *
     * @param reference the reference string
     * @param target    the schema object being referenced
     * @return the json reference instance
     */
    static JsonReference create(String reference, Object target) {
        Objects.requireNonNull(reference, "Json reference cannot be null");
        return new JsonReferenceImpl(reference, target);
    }

    /**
     * Gets the {@link JsonObject} representation of th Json reference instance.
     *
     * @return the json reference as json
     */
    JsonObject toJson();

    /**
     * Gets the reference string with the location of the target schema object.
     *
     * @return the reference string
     */
    String getReference();

    /**
     * Gets the schema object this is being referenced by this Json reference.
     *
     * @param <T> type parameter indicating the type of schema object to return
     * @return the schema object
     */
    <T> T getTarget();

    /**
     * Gets the json key of the node being referenced. Corresponds to the final part of the json reference string.
     *
     * @return the target key
     */
    String getTargetKey();

    /**
     * Gets the json reference as string.
     *
     * @return the json reference
     */
    @Override
    String toString();

    /**
     * Indicates whether some other object is "equal to" this one.
     * <p>
     * Two json references are considered equal when their reference strings are equal. The target object is ignored.
     *
     * @param other the object to compare
     * @return {@code true} when equal, {@code false} otherwise
     */
    @Override
    boolean equals(Object other);

    /**
     * Gets the hash code of the Json reference.
     * <p>
     * The result equals the hash code of the reference string.
     *
     * @return the hash code
     */
    @Override
    int hashCode();
}
