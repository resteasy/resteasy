Skeleton Key IDM
================
These modules are a set of custom security protocols with the following goals:

* Simple token formats
* Java EE compatibility
* Reduce IDM handshaking
* Leverage PKI wherever possible
* Tight JBoss AS7 Integration
* Standalone JAX-RS-only integration
* OAuth2 login
* OAuth2 bearer tokens

It will include a sample IDM server with Infinispan as its persistence store.  The IDM will be a RESTful interface that
is designed to be an anybody-can-use cloud service.

IDM Server Design Notes
============
* Realms are a collection of users, resources, roles, role, and scope mappings.
* Scope is an OAuth2 concept.  A user can ask another user for permission to act on behalf of them.  Scope mappings
define which roles a user is allowed to ask permission for.
* A Realms can have user-role mappings
* A realm can have scope mappings
* A resource represents a specific web site or web service.  It is not required
* A resource can have specific roles and user-role mappings
* A resource can have specific scope mappings
* User names must be unique within a Realm
* Resource names must be unique per Realm
* Authentication is a combination of form parameters and/or client-cert verification.
* realms define a set of required credentials.

Realm Creation
---------------
1. No authentication required to create a realm
2. A domain is disabled for use until a user with admin priviledges enables it.
3. A domain must have a least one admin user assigned to it


User Creation
---------------
1. Usernames must be unique within a domain
2. Realms define a set of required credentials.  users will not be enabled until they have set up all of their required credentials

Resource Creation
---------------
1. Resource names are unique within a domain
2. A Resource represents a website or service and has a Base URL associated with it.
3. A resource defines a set of roles it provides
4. Resources cannot belong to multiple domains




