package org.jboss.resteasy.test.cdi.validation.resource;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.ext.Provider;

/**
 *
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 *          Copyright Jul 20, 2015
 */
@Provider
@ApplicationPath("/test")
public class TestApplication extends Application
{
}
