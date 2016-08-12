# Vert.x GraphQL Service Proxy

This project provides support for creating service proxies for `GraphQLSchema` definitions and on the Vert.x event bus.
Schema proxies are created by wrapping the various GraphQL classes that exist in the `graphql.schemas` package of [graphql-java](https://github.com/graphql-java/graphql-java) as Vert.x data objects that can be marshalled to JSON and sent on the event bus.

Clients can subsequently instantiate a proxy of the GraphQL schema. In the proxy any data fetchers and type resolvers that existed in the original schema have been replaced with a service proxy that delegates back to the original schema in the service provider.

This project will be applied to [vertx-graphql-service-discovery](https://github.com/engagingspaces/vertx-graphql-service-discovery) where it facilitates support for:

- Running a query that is completely off-loaded to the schema publisher by sending the query string and awaiting the JSON result (the default)
- Running a query on the consumer-side using a fine-grained mode of communication (where a message is sent to the schema publisher for each `TypeResolver` and `DataFetcher` that is encountered during query execution)
- Mixed mode of communication where a query is partially executed in the consumer up to a certain point (dependent on metadata) after which the schema publisher takes care of processing the remainder

More information and code coming soon..
