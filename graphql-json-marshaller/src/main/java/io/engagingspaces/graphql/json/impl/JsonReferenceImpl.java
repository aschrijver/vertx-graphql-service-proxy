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

import io.engagingspaces.graphql.json.JsonReference;
import io.vertx.core.json.JsonObject;

import static io.engagingspaces.graphql.json.impl.JsonReferenceHelper.ROOT_REFERENCE;
import static io.engagingspaces.graphql.json.impl.JsonReferenceHelper.SLASH;
import static io.engagingspaces.graphql.schema.SchemaContext.EMPTY;

/**
 * Implementation of {@link JsonReference}.
 *
 * @author <a href="https://github.com/aschrijver/">Arnold Schrijver</a>
 */
public class JsonReferenceImpl implements JsonReference {

    private String reference;
    private Object target;

    /**
     * Constructor for json reference.
     *
     * @param reference the reference string
     * @param target    the target object being referenced
     */
    public JsonReferenceImpl(String reference, Object target) {
        this.reference = reference.length() == 0 ? ROOT_REFERENCE : reference;
        this.target = target;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonObject toJson() {
        return new JsonObject().put(JsonReference.REF_KEY, reference);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getReference() {
        return reference;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getTarget() {
        return (T) target;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTargetKey() {
        return ROOT_REFERENCE.equals(reference) ? EMPTY : reference.substring(reference.lastIndexOf(SLASH) + 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return reference;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof JsonReference &&
                this.reference.equals(((JsonReference) other).getReference());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return reference.hashCode();
    }
}
