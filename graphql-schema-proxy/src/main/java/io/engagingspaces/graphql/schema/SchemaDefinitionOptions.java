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
import io.engagingspaces.graphql.marshaller.json.impl.JsonObjectHelper;
import io.vertx.codegen.annotations.Fluent;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

/**
 * Configuration options for {@link SchemaDefinition}.
 *
 * @author <a href="https://github.com/aschrijver/">Arnold Schrijver</a>
 */
public class SchemaDefinitionOptions {

    private String schemaName;
    private DeliveryOptions deliveryOptions;
    private SchemaProxyType proxyType = SchemaProxyType.ServiceProxy;
    private boolean isInternal = true;

    /**
     * Creates a new (empty) options instance.
     */
    public SchemaDefinitionOptions() {}

    /**
     * Clones an existing options instance.
     *
     * @param other the schema definition options to clone
     */
    public SchemaDefinitionOptions(SchemaDefinitionOptions other) {
        this.schemaName = other.schemaName;
        this.deliveryOptions = other.deliveryOptions;
        this.proxyType = other.proxyType;
        this.isInternal = other.isInternal;
    }

    /**
     * Creates a {@link SchemaDefinitionOptions} from its json representation.
     *
     * @param json the serialized options instance
     */
    public SchemaDefinitionOptions(JsonObject json) {
        Objects.requireNonNull(json, "Schema definition options json cannot be null");
        this.schemaName = json.getString("schemaName");
        this.deliveryOptions = new DeliveryOptions(json.getJsonObject("deliveryOptions"));
        this.proxyType = Enum.valueOf(SchemaProxyType.class, json.getString("proxyType"));
        this.isInternal = json.getBoolean("isInternal");
    }

    /**
     * Converts this schema definition options to json.
     *
     * @return the serialized json object
     */
    public JsonObject toJson() {
        return new JsonObject()
                .put("schemaName", schemaName)
                .put("proxyType", proxyType)
                .put("deliveryOptions", deliveryOptionsToJson(deliveryOptions))
                .put("isInternal", isInternal);
    }

    /**
     * Gets the name of the schema definition.
     * <p>
     * If no name is provided this defaults the type name of {@link GraphQLSchema#getQueryType()} in the schema.
     *
     * @return the schema definition name
     */
    public String getSchemaName() {
        return schemaName;
    }

    /**
     * Sets the name of the schema definition.
     *
     * @param schemaName the schema definition name
     * @return the schema definition options for fluent coding
     */
    @Fluent
    public SchemaDefinitionOptions setSchemaName(String schemaName) {
        this.schemaName = schemaName;
        return this;
    }

    /**
     * Gets the delivery options to use for service proxy invocations.
     *
     * @return the delivery options
     */
    public DeliveryOptions getDeliveryOptions() {
        return deliveryOptions;
    }

    /**
     * Sets the delivery options to use for service proxy invocations.
     *
     * @param deliveryOptions the delivery options
     * @return the schema definition options for fluent coding
     */
    @Fluent
    public SchemaDefinitionOptions setDeliveryOptions(DeliveryOptions deliveryOptions) {
        Objects.requireNonNull(deliveryOptions, "Delivery options cannot be null");
        this.deliveryOptions = deliveryOptions;
        return this;
    }

    /**
     * Gets the flag that determines whether this schema is for internal use only (the default) or not.
     * <p>
     * The exact meaning of the flag depends on the implementor.
     *
     * @return true, if the schema is internal, false otherwise
     */
    public boolean isInternal() {
        return isInternal;
    }

    /**
     * Sets the schema as either internal (the default) or not.
     *
     * @param internal {@code true} to mark the schema as internal, {@code false} otherwise
     * @return the schema definition options for fluent coding
     */
    @Fluent
    public SchemaDefinitionOptions setInternal(boolean internal) {
        this.isInternal = internal;
        return this;
    }

    /**
     * Gets the type of schema proxy to create for the schema definition.
     *
     * @return the schema proxy type
     */
    public SchemaProxyType getProxyType() {
        return proxyType;
    }

    /**
     * Sets the type of service proxy to create for the schema definition.
     *
     * @param proxyType the schema proxy type
     * @return the schema definition options for fluent coding
     */
    @Fluent
    public SchemaDefinitionOptions setProxyType(SchemaProxyType proxyType) {
        Objects.requireNonNull(proxyType, "Schema proxy type cannot be null");
        this.proxyType = proxyType;
        return this;
    }

    private JsonObject deliveryOptionsToJson(DeliveryOptions options) {
        return JsonObjectHelper.jsonObject()
                .put("sendTimeout", options.getSendTimeout())
                .putIfPresent("codecName", options.getCodecName())
                // TODO Test with MultiMap values
                .putIf("headers", options.getHeaders(), headers -> headers.size() > 0, headers -> headers);
    }
}
