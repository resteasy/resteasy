package org.jboss.resteasy.test.finegrain;

import org.junit.Test;

import javax.ws.rs.core.Link;
import java.net.URI;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class LinkTest
{
   @Test
   public void testRelativized() throws Exception
   {
      URI uri = new URI("a").relativize(new URI("a/d/e"));
      System.out.println(uri);

      Link link = Link.fromUri("a/d/e")
                      .rel("update").type("text/plain")
                      .buildRelativized(new URI("a"));
      System.out.println(link.toString());

      link = Link.fromUri("a/d/e")
              .rel("update").type("text/plain")
              .baseUri("http://localhost/")
              .buildRelativized(new URI("http://localhost/a"));
      System.out.println(link.toString());

   }
}
