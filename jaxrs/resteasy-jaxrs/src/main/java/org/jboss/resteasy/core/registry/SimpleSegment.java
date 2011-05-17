package org.jboss.resteasy.core.registry;

import org.jboss.resteasy.core.ResourceInvoker;
import org.jboss.resteasy.specimpl.UriInfoImpl;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.NotFoundException;
import org.jboss.resteasy.util.Encode;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class SimpleSegment extends RootSegment
{
   protected String segment;

   public SimpleSegment(String segment)
   {
      this.segment = segment;
   }

   public String getSegment()
   {
      return segment;
   }

   public ResourceInvoker matchSimple(HttpRequest request, String path, int start)
   {
      UriInfoImpl uriInfo = (UriInfoImpl) request.getUri();
      if (start + segment.length() == path.length()) // we've reached end of string
      {
         ResourceInvoker invoker = match(request.getHttpMethod(), request);
         if (invoker == null)
            throw new NotFoundException("Could not find resource for relative : " + path + " of full path: " + request.getUri().getRequestUri());

         uriInfo.pushMatchedURI(path, Encode.decode(path));
         return invoker;
      }
      else
      {
         try
         {
            return matchChildren(request, path, start + segment.length() + 1); // + 1 to ignore '/'
         }
         catch (Failure e)
         {
            if (locator != null)
            {
               String matched = path.substring(0, start + segment.length());
               uriInfo.pushMatchedURI(matched, Encode.decode(matched));
               return locator;
            }
            else throw e;
         }
      }

   }

}
