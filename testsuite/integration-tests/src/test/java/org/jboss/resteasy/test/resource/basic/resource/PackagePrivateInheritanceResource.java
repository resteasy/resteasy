/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.test.resource.basic.resource;

import jakarta.ws.rs.Path;

/**
 * Public resource class that inherits its resource method from a package-private superclass.
 * Used to verify RESTEASY-3621: invoking inherited methods from package-private classes
 * must not throw IllegalAccessException.
 */
@Path("/package-private-inheritance")
public class PackagePrivateInheritanceResource extends PackagePrivateAbstractResource {
}
