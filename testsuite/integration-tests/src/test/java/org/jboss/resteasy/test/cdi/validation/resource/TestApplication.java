package org.jboss.resteasy.test.cdi.validation.resource;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import javax.ws.rs.ext.Provider;

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
