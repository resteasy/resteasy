Skeleton Key IDM
================
These modules are a set of custom security protocols with the following goals:

* Simple token formats
* Java EE compatibility
* Reduce IDM handshaking
* Leverage PKI wherever possible
* Tight JBoss AS7 Integration
* JAX-RS-only integration

It will include a sample IDM server with Infinispan as its persistence store.  The IDM will be a RESTful interface that
is designed to be an anybody-can-use cloud service.

IDM Server Design Notes
============
* Domains are a collection of resources, users and their role mappings.
* Domain names are unique and must be unique in the datastore
* A user is owned solely by and defined in one domain.
* Users names must be unique within a domain

Authentication Mechanisms
-------------------------
* Authentication of a user by the service must be client-cert or by an email verification protocol
* Email verification is a user enters in their username and receive an email with a temporary URL they can enter
other credentials (like password).  This is to avoid middleman attacks designed to phish username/passwords by pretending
to be the authentication server.  A successful login will set a persistent cookie that will be used to avoid having
to do email verification.
* We'll probably just do username/password first rev.

Domain Creation
---------------
1. No authentication required to create a domain
2. A domain is disabled for use until a user with admin priviledges enables it.
3. A domain must have a least one admin user assigned to it

User Creation
---------------
1. Usernames must be unique within a domain
2. users will not be enabled until they request a client certificate in domains that use client certificate authentication

Resource Creation
---------------
1. Resource names are unique within a domain
2. A Resource represents a website or service and has a Base URL associated with it.
3. A resource defines a set of roles it provides
4. Resources cannot belong to multiple domains

Assigning roles
----------------
1. User/role/resource data creates a mapping for roles/resources and users within a domain
2. Additionally you can provide a surrogate that must be a userid.  if a surrogate is specified in the mapping, then
only that surrogate can make invocations for that user, on behalf of that user.
3. Surrogates can only be used with client-certificate authentication. (Don't know if this should be done or not)

Inifinispan data model
-----------------
/domains/{id}
/domains/{id}/users -> map of username->id (for login)
/resources/{id} -> Resource
/resources/{id}/domain -> domainid string
/roles/{id} -> String name
/roles/{id}/resource -> string resource id
/users/{id}
/users/{id}/domain
/rolemappings/{id}
/rolemappings/{id}/resource
/userattributes/{id}
/userattributes/{id}/user
/usercredential/{id}
/usercredential/{id]/user



