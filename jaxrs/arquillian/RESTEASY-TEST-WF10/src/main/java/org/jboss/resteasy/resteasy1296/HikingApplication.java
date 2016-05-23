/*
 * Hibernate Validator, declare and validate application constraints
 *
 * License: Apache License, Version 2.0
 * See the license.txt file in the root directory or <http://www.apache.org/licenses/LICENSE-2.0>.
 */
package org.jboss.resteasy.resteasy1296;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * @author Gunnar Morling
 */
@ApplicationPath("/hiking-manager")
public class HikingApplication extends Application {
}
