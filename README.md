# Vert.x GraphQL Schema Proxy

This project provides support for creating service proxies for `GraphQLSchema` definitions and on the Vert.x event bus.
Schema proxies are created by wrapping the various GraphQL classes that exist in the `graphql.schemas` package of [graphql-java](https://github.com/graphql-java/graphql-java) as Vert.x data objects that can be marshalled to JSON and sent on the event bus.

Clients can subsequently instantiate a proxy of the GraphQL schema. In the proxy any data fetchers and type resolvers that existed in the original schema have been replaced with a service proxy that delegates back to the original schema in the service provider.

More information and code coming soon..
