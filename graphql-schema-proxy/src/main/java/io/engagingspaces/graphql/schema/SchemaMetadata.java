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
import io.engagingspaces.graphql.marshaller.SchemaMarshallerOptions;
import io.engagingspaces.graphql.proxy.GraphQLSchemaProxy;
import io.engagingspaces.graphql.proxy.impl.SchemaProxyContext;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

/**
 * Metadata that is required for creation of service proxies of {@link GraphQLSchema}.
 *
 * @author <a href="https://github.com/aschrijver/">Arnold Schrijver</a>
 */
@DataObject
public class SchemaMetadata {

    /**
     * Key to the list of root query field names stored in schema metadata.
     */
    public static final String METADATA_QUERIES = "queries";

    /**
     * Key to the list of mutation field names stored in schema metadata.
     */
    public static final String METADATA_MUTATIONS = "mutations";

    private final JsonObject proxyJson;
    private final JsonObject metadata;
    private final SchemaDefinitionOptions options;
    private final String serviceAddress;

    private GraphQLSchemaProxy schemaProxy;

    protected SchemaMetadata(JsonObject proxyJson, JsonObject metadata,
                             SchemaDefinitionOptions options, String serviceAddress) {
        this.proxyJson = proxyJson == null ? new JsonObject() : proxyJson;
        this.metadata = metadata == null ? new JsonObject() : metadata;
        this.options = options == null ? new SchemaDefinitionOptions() : options;
        this.serviceAddress = serviceAddress;
    }

    /**
     * Creates a new instance from its json representation.
     *
     * @param json the serialized json object
     */
    public SchemaMetadata(JsonObject json) {
        Objects.requireNonNull(json, "Schema metadata json cannot be null");
        this.proxyJson = json.getJsonObject("proxyJson", new JsonObject());
        this.metadata = json.getJsonObject("metadata");
        this.options = new SchemaDefinitionOptions(json.getJsonObject("schemaDefinitionOptions"));
        this.serviceAddress = json.getString("serviceAddress");
    }

    /**
     * Clones an existing schema metadata.
     *
     * @param other the schema metadata to clone
     */
    public SchemaMetadata(SchemaMetadata other) {
        Objects.requireNonNull(other, "Schema metadata cannot be null");
        this.proxyJson = other.proxyJson;
        this.metadata = other.metadata;
        this.options = other.options;
        this.serviceAddress = other.serviceAddress;
    }

    /**
     * Creates a new schema metadata instance.
     *
     * @param proxyJson the marshaled graphql schema for {@link SchemaProxyType#ProxyClient} creation, or null
     * @param metadata  the additional metadata to pass to the service proxy
     * @param options   the schema definition options
     * @return the schema metadata instance
     */
    public static SchemaMetadata create(JsonObject proxyJson, JsonObject metadata, SchemaDefinitionOptions options) {
        String address = SchemaDefinition.ADDRESS_PREFIX + "." + options.getSchemaName();
        return new SchemaMetadata(proxyJson, metadata, options, address);
    }

    /**
     * Converts the schema metadata to json.
     *
     * @return the serialized schema metadata
     */
    public JsonObject toJson() {
        return new JsonObject()
                .put("proxyJson", proxyJson)
                .put("metadata", metadata)
                .put("schemaDefinitionOptions", options.toJson())
                .put("serviceAddress", serviceAddress);
    }

    /**
     * Gets the address of the service proxy to use for the schema definition.
     *
     * @return the service address
     */
    public String getServiceAddress() {
        return serviceAddress;
    }

    /**
     * Gets the flag that determines whether to use a client proxy at the schema consumer side.
     *
     * @return {@code true} when using a client proxy, {@code false} otherwise
     */
    public boolean isClientProxy() {
        return SchemaProxyType.ProxyClient.equals(options.getProxyType()) && !proxyJson.equals(new JsonObject());
    }

    /**
     * Gets the client proxy instance, if the schema proxy type is {@link SchemaProxyType#ProxyClient}.
     *
     * @param vertx the vert.x instance to pass to the proxy
     * @return the graphql client schema proxy
     */
    @GenIgnore
    public GraphQLSchemaProxy getClientProxy(Vertx vertx) {
        if (schemaProxy == null) {
            schemaProxy = new GraphQLSchemaProxy(vertx, proxyJson,
                    SchemaProxyContext.createUnmarshalingContext(SchemaMarshallerOptions.create(), proxyJson));
        }
        return schemaProxy;
    }

    /**
     * Gets the additional metadata that is to be passed to the service proxy (implementer-specific).
     *
     * @return the additional metadata
     */
    public JsonObject metadata() {
        return metadata;
    }

    /**
     * Gets the schema definition options to were applied to the schema definition.
     *
     * @return the schema definition options
     */
    public SchemaDefinitionOptions options() {
        return options;
    }
}
