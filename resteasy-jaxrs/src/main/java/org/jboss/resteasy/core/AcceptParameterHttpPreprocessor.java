package org.jboss.resteasy.core;

import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * <p>Enables content negotiation through a query parameter, instead of the Accept Header.</p>
 * <p>To enable this feature, use the context-param in web.xml:</p>
 * <p>
 * <code>
 * &lt;context-param&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-name&gt;<strong>resteasy.media.type.param.mapping</strong>&lt;/param-name&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-value&gt;<i>someName</i>&lt;/param-value&gt;<br>
 * &lt;/context-param&gt;<br>
 * </code>
 * <p>So, in a request like
 * <code>http://service.foo.com/resouce?someName=application/xml</code>
 * the application/xml media type will received the highest priority in the content negotiation.
 * <p>In the cases where the request contains both the parameter and the Accept header, the parameter will be more relevant.
 * <p>It is possible to left the <code>param-value</code> empty, what will cause the processor to look for an <strong>accept</strong> parameter.
 *
 * @author <a href="leandro.ferro@gmail.com">Leandro Ferro Luzia</a>
 * @version $Revision: 1.2 $
 */
@PreMatching
public class AcceptParameterHttpPreprocessor implements ContainerRequestFilter
{

   private final String paramMapping;

   /**
    * Create a new AcceptParameterHttpPreprocessor.
    *
    * @param paramMapping The name of query parameter that will be used to do the content negotiation
    */
   public AcceptParameterHttpPreprocessor(String paramMapping)
   {
      if (paramMapping == null || paramMapping.matches("\\s+"))
         throw new IllegalArgumentException(Messages.MESSAGES.constructorMappingInvalid());
      this.paramMapping = paramMapping;
   }

   @Override
   public void filter(ContainerRequestContext request) throws IOException
   {
      MultivaluedMap<String, String> params = request.getUriInfo().getQueryParameters(false);

      if (params != null)
      {
         List<String> accepts = params.get(paramMapping);

         if (accepts != null && !accepts.isEmpty())
         {
            for (String accept : accepts)
            {
               try
               {
                  accept = URLDecoder.decode(accept, StandardCharsets.UTF_8.name());
                  request.getHeaders().add(HttpHeaders.ACCEPT, accept);
               }
               catch (UnsupportedEncodingException e)
               {
                  throw new RuntimeException(e);
               }
            }

         }
      }

   }

}
