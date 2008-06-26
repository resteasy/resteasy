/**
 *
 */
package org.resteasy.test;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.resteasy.Dispatcher;

/**
 * @author <a href="mailto:ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision:$
 */
public abstract class BaseResourceTest
{

   protected static Dispatcher dispatcher;

   @BeforeClass
   public static void before() throws Exception
   {
      dispatcher = EmbeddedContainer.start();
   }

   @AfterClass
   public static void after() throws Exception
   {
      EmbeddedContainer.stop();
   }

   /**
    * @param resource
    */
   public void addPerRequestResource(Class<?> resource)
   {
      dispatcher.getRegistry().addPerRequestResource(resource);
   }
}
