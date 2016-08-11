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

package io.engagingspaces.graphql.marshaller.schema;

/**
 * Interface for GraphQL schema objects that are not stand-alone, but an aggregate part of another schema object.
 * <p>
 * Examples are the fields of a type, or arguments of a field. Such objects will have an association to their parent.
 *
 * @param <T> type parameter that defines the type being decorated
 * @param <P> type parameter that defines the type of the parent object
 * @author <a href="https://github.com/aschrijver/">Arnold Schrijver</a>
 */
public interface SchemaChildDecorator<T, P> extends SchemaDecorator<T> {

    /**
     * @return the parent object
     */
    P getParent();
}
