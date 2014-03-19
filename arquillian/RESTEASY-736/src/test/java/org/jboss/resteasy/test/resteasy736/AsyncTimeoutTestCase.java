package org.jboss.resteasy.test.resteasy736;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.resteasy736.TestApplication;
import org.jboss.resteasy.resteasy736.TestResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.runner.RunWith;

/**
 *
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Aug 3, 2012
 */
@RunWith(Arquillian.class)
@RunAsClient
public abstract class AsyncTimeoutTestCase
{
   @Deployment
   public static Archive<?> createTestArchive()
   {
      return ShrinkWrap.create(WebArchive.class, "RESTEASY-736.war")
            .addClasses(TestApplication.class, TestResource.class)
            .addAsWebInfResource("web.xml");
   }

}
