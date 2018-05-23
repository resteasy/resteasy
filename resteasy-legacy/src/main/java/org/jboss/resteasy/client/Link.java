package org.jboss.resteasy.client;

import org.jboss.resteasy.client.ClientExecutor;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.specimpl.LinkBuilderImpl;
import org.jboss.resteasy.specimpl.MultivaluedMapImpl;

import javax.ws.rs.core.MultivaluedMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of <a href="http://tools.ietf.org/html/draft-nottingham-http-link-header-06">Link Headers v6</a>
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 * 
 * @deprecated Replaced by javax.ws.rs.core.Link in jaxrs-api module.
 * 
 * @see <a href="https://jcp.org/en/jsr/detail?id=339">jaxrs-api</a>
 * @see javax.ws.rs.core.Link
 */
@Deprecated
public class Link
{
   protected String title;
   protected String relationship;
   protected String href;
   protected String type;
   protected MultivaluedMap<String, String> extensions = new MultivaluedMapImpl<String, String>();
   protected ClientExecutor executor;

   public Link()
   {
   }

   public Link(String title, String relationship, String href, String type, MultivaluedMap<String, String> extensions)
   {
      this.relationship = relationship;
      this.href = href;
      this.type = type;
      this.title = title;
      if (extensions != null) this.extensions = extensions;
   }

   public javax.ws.rs.core.Link toJaxrsLink()
   {
      javax.ws.rs.core.Link.Builder builder = new LinkBuilderImpl();
      builder.rel(getRelationship());
      builder.title(getTitle());
      builder.type(getType());
      builder.uri(getHref());
      for (Map.Entry<String, List<String>> entry : getExtensions().entrySet())
      {
         for (String val : entry.getValue())
         {
            builder.param(entry.getKey(), val);
         }
      }
      return builder.build();
   }

   public String getRelationship()
   {
      return relationship;
   }

   public void setRelationship(String relationship)
   {
      this.relationship = relationship;
   }

   public String getHref()
   {
      return href;
   }

   public void setHref(String href)
   {
      this.href = href;
   }

   public String getType()
   {
      return type;
   }

   public void setType(String type)
   {
      this.type = type;
   }

   public String getTitle()
   {
      return title;
   }

   public void setTitle(String title)
   {
      this.title = title;
   }

   public MultivaluedMap<String, String> getExtensions()
   {
      return extensions;
   }

   public ClientExecutor getExecutor()
   {
      return executor;
   }

   public void setExecutor(ClientExecutor executor)
   {
      this.executor = executor;
   }

   public ClientRequest request()
   {
      if (executor != null)
      {
         return new ClientRequest(href, executor);
      }
      else
      {
         return new ClientRequest(href);
      }
   }

   public ClientRequest request(ClientExecutor executor)
   {
      return new ClientRequest(href, executor);
   }

   public String toString()
   {
      StringBuffer buf = new StringBuffer("<");
      buf.append(href).append(">");
      if (relationship != null)
      {
         buf.append("; rel=\"").append(relationship).append("\"");
      }
      if (type != null)
      {
         buf.append("; type=\"").append(type).append("\"");
      }
      if (title != null)
      {
         buf.append("; title=\"").append(title).append("\"");
      }
      for (String key : getExtensions().keySet())
      {
         List<String> values = getExtensions().get(key);
         for (String val : values)
         {
            buf.append("; ").append(key).append("=\"").append(val).append("\"");
         }
      }
      return buf.toString();
   }

}
