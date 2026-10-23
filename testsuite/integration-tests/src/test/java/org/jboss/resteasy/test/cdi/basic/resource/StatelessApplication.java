/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.test.cdi.basic.resource;

import jakarta.ejb.Stateless;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/**
 * A stateless session bean {@link Application} subclass which does not declare a scope. It is not a nested class, as
 * an enterprise bean must not be one.
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
@Stateless
@ApplicationPath("/")
public class StatelessApplication extends Application {
}
